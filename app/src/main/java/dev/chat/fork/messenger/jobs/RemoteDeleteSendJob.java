package dev.chat.fork.messenger.jobs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import org.signal.core.util.SetUtil;
import org.signal.core.util.Util;
import org.signal.core.util.logging.Log;
import org.signal.libsignal.protocol.NoSessionException;
import dev.chat.fork.messenger.database.GroupTable;
import dev.chat.fork.messenger.database.MessageTable;
import dev.chat.fork.messenger.database.NoSuchMessageException;
import dev.chat.fork.messenger.database.RecipientTable.RegisteredState;
import dev.chat.fork.messenger.database.SignalDatabase;
import dev.chat.fork.messenger.database.model.DistributionListId;
import dev.chat.fork.messenger.database.model.MessageId;
import dev.chat.fork.messenger.database.model.MessageRecord;
import dev.chat.fork.messenger.database.model.MmsMessageRecord;
import dev.chat.fork.messenger.database.model.RecipientRecord;
import dev.chat.fork.messenger.dependencies.AppDependencies;
import dev.chat.fork.messenger.jobmanager.Job;
import dev.chat.fork.messenger.jobmanager.JobManager;
import dev.chat.fork.messenger.jobmanager.JsonJobData;
import dev.chat.fork.messenger.jobmanager.impl.SealedSenderConstraint;
import dev.chat.fork.messenger.messages.GroupSendUtil;
import dev.chat.fork.messenger.net.NotPushRegisteredException;
import dev.chat.fork.messenger.recipients.Recipient;
import dev.chat.fork.messenger.recipients.RecipientId;
import dev.chat.fork.messenger.recipients.RecipientUtil;
import dev.chat.fork.messenger.transport.RetryLaterException;
import dev.chat.fork.messenger.transport.UndeliverableMessageException;
import dev.chat.fork.messenger.util.GroupUtil;
import org.whispersystems.signalservice.api.crypto.ContentHint;
import org.whispersystems.signalservice.api.crypto.UntrustedIdentityException;
import org.whispersystems.signalservice.api.messages.SendMessageResult;
import org.whispersystems.signalservice.api.messages.SignalServiceDataMessage;
import org.whispersystems.signalservice.api.push.exceptions.ServerRejectedException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RemoteDeleteSendJob extends BaseJob {

  public static final String KEY = "RemoteDeleteSendJob";

  private static final String TAG = Log.tag(RemoteDeleteSendJob.class);

  private static final String KEY_MESSAGE_ID              = "message_id";
  private static final String KEY_RECIPIENTS              = "recipients";
  private static final String KEY_INITIAL_RECIPIENT_COUNT = "initial_recipient_count";

  private final long              messageId;
  private final List<RecipientId> recipients;
  private final int               initialRecipientCount;


  @WorkerThread
  public static @NonNull JobManager.Chain create(long messageId)
      throws NoSuchMessageException
  {
    MessageRecord message = SignalDatabase.messages().getMessageRecord(messageId);

    RecipientId conversationRecipientId = SignalDatabase.threads().getRecipientIdForThreadId(message.getThreadId());

    if (conversationRecipientId == null) {
      throw new AssertionError("We have a message, but couldn't find the thread!");
    }

    RecipientRecord conversationRecipient = SignalDatabase.recipients().getRecord(conversationRecipientId);

    List<RecipientId> recipients;
    if (conversationRecipient.getDistributionListId() != null) {
      recipients = SignalDatabase.storySends().getRemoteDeleteRecipients(message.getId(), message.getTimestamp());
      if (recipients.isEmpty()) {
        return AppDependencies.getJobManager().startChain(MultiDeviceStorySendSyncJob.create(message.getDateSent(), messageId));
      }
    } else {
      recipients = conversationRecipient.getGroupId() != null
                   ? SignalDatabase.groups().getGroupMemberIds(conversationRecipient.getGroupId(), GroupTable.MemberSet.FULL_MEMBERS_INCLUDING_SELF).stream().collect(Collectors.toList())
                   : Stream.of(conversationRecipient.getId()).collect(Collectors.toList());
    }

    recipients.remove(Recipient.self().getId());

    RemoteDeleteSendJob sendJob = new RemoteDeleteSendJob(messageId,
                                                          recipients,
                                                          recipients.size(),
                                                          new Parameters.Builder()
                                                                        .setQueue(conversationRecipient.getId().toQueueKey())
                                                                        .addConstraint(SealedSenderConstraint.KEY)
                                                                        .setLifespan(TimeUnit.DAYS.toMillis(1))
                                                                        .setMaxAttempts(Parameters.UNLIMITED)
                                                                        .build());

    if (conversationRecipient.getDistributionListId() != null) {
      return AppDependencies.getJobManager()
                            .startChain(sendJob)
                            .then(MultiDeviceStorySendSyncJob.create(message.getDateSent(), messageId));
    } else {
      return AppDependencies.getJobManager().startChain(sendJob);
    }
  }

  private RemoteDeleteSendJob(long messageId,
                              @NonNull List<RecipientId> recipients,
                              int initialRecipientCount,
                              @NonNull Parameters parameters)
  {
    super(parameters);

    this.messageId             = messageId;
    this.recipients            = recipients;
    this.initialRecipientCount = initialRecipientCount;
  }

  @Override
  public @Nullable byte[] serialize() {
    return new JsonJobData.Builder().putLong(KEY_MESSAGE_ID, messageId)
                                    .putString(KEY_RECIPIENTS, RecipientId.toSerializedList(recipients))
                                    .putInt(KEY_INITIAL_RECIPIENT_COUNT, initialRecipientCount)
                                    .serialize();
  }

  @Override
  public @NonNull String getFactoryKey() {
    return KEY;
  }

  @Override
  protected void onRun() throws Exception {
    if (!Recipient.self().isRegistered()) {
      throw new NotPushRegisteredException();
    }

    MessageTable  db      = SignalDatabase.messages();
    MessageRecord message = SignalDatabase.messages().getMessageRecord(messageId);

    long      targetSentTimestamp   = message.getDateSent();
    RecipientId conversationRecipientId = SignalDatabase.threads().getRecipientIdForThreadId(message.getThreadId());

    if (conversationRecipientId == null) {
      throw new AssertionError("We have a message, but couldn't find the thread!");
    }

    RecipientRecord conversationRecipient = SignalDatabase.recipients().getRecord(conversationRecipientId);

    if (!message.isOutgoing()) {
      throw new IllegalStateException("Cannot delete a message that isn't yours!");
    }

    boolean isRegistered = conversationRecipient.getGroupId() != null ? !conversationRecipient.getGroupId().isMms()
                                                                       : (conversationRecipient.getDistributionListId() != null || conversationRecipient.getRegistered() == RegisteredState.REGISTERED);

    if (!isRegistered) {
      Log.w(TAG, "Unable to remote delete non-push messages");
      return;
    }

    if (conversationRecipient.getGroupId() != null && conversationRecipient.getGroupId().isV1()) {
      Log.w(TAG, "Unable to remote delete messages in GV1 groups");
      return;
    }

    if (conversationRecipient.getGroupId() != null && conversationRecipient.getGroupId().isV2() && !SignalDatabase.groups().isActive(conversationRecipient.getGroupId())) {
      Log.w(TAG, "Unable to remote delete messages in terminated or inactive groups");
      return;
    }

    List<Recipient>   possible = recipients.stream().map(Recipient::resolved).collect(Collectors.toList());
    List<Recipient>   eligible = RecipientUtil.getEligibleForSending(recipients.stream().map(Recipient::resolved).filter(Recipient::getHasServiceId).collect(Collectors.toList()));
    List<RecipientId> skipped  = SetUtil.difference(possible, eligible).stream().map(Recipient::getId).collect(Collectors.toList());

    boolean            isForStory         = message.isMms() && (((MmsMessageRecord) message).getStoryType().isStory() || ((MmsMessageRecord) message).getParentStoryId() != null);
    DistributionListId distributionListId = isForStory ? message.getToRecipient().getDistributionListId().orElse(null) : null;

    GroupSendJobHelper.SendResult sendResult = deliver(conversationRecipient, eligible, targetSentTimestamp, isForStory, distributionListId);

    for (Recipient completion : sendResult.completed) {
      recipients.remove(completion.getId());
    }

    for (RecipientId unregistered : sendResult.unregistered) {
      SignalDatabase.recipients().markUnregistered(unregistered);
    }

    for (RecipientId skip : skipped) {
      recipients.remove(skip);
    }

    List<RecipientId> totalSkips = Util.join(skipped, sendResult.skipped);

    Log.i(TAG, "Completed now: " + sendResult.completed.size() + ", Skipped: " + totalSkips.size() + ", Remaining: " + recipients.size());

    if (totalSkips.size() > 0 && message.getToRecipient().isGroup()) {
      SignalDatabase.groupReceipts().setSkipped(totalSkips, messageId);
    }

    if (recipients.isEmpty()) {
      db.markAsSent(messageId);
    } else {
      Log.w(TAG, "Still need to send to " + recipients.size() + " recipients. Retrying.");
      throw new RetryLaterException();
    }
  }

  @Override
  protected boolean onShouldRetry(@NonNull Exception e) {
    if (e instanceof ServerRejectedException) return false;
    if (e instanceof NotPushRegisteredException) return false;
    return e instanceof IOException ||
           e instanceof RetryLaterException;
  }

  @Override
  public long getNextRunAttemptBackoff(int pastAttemptCount, @NonNull Exception exception) {
    return SendJobUtil.getBackoffMillisFromException(this, TAG, pastAttemptCount, exception, () -> super.getNextRunAttemptBackoff(pastAttemptCount, exception));
  }

  @Override
  public void onFailure() {
    Log.w(TAG, "Failed to send remote delete to all recipients! (" + (initialRecipientCount - recipients.size() + "/" + initialRecipientCount + ")") );
  }

  private @NonNull GroupSendJobHelper.SendResult deliver(@NonNull RecipientRecord conversationRecipient,
                                                         @NonNull List<Recipient> destinations,
                                                         long targetSentTimestamp,
                                                         boolean isForStory,
                                                         @Nullable DistributionListId distributionListId)
      throws IOException, UntrustedIdentityException, NoSessionException, UndeliverableMessageException
  {
    SignalServiceDataMessage.Builder dataMessageBuilder = SignalServiceDataMessage.newBuilder()
                                                                                  .withTimestamp(System.currentTimeMillis())
                                                                                  .withRemoteDelete(new SignalServiceDataMessage.RemoteDelete(targetSentTimestamp));

    if (conversationRecipient.getGroupId() != null) {
      GroupUtil.setDataMessageGroupContext(context, dataMessageBuilder, conversationRecipient.getGroupId().requirePush());
    }

    SignalServiceDataMessage dataMessage = dataMessageBuilder.build();
    List<SendMessageResult>  results     = GroupSendUtil.sendResendableDataMessage(context,
                                                                                   conversationRecipient.getGroupId() != null ? conversationRecipient.getGroupId().requireV2() : null,
                                                                                   distributionListId,
                                                                                   destinations,
                                                                                   false,
                                                                                   ContentHint.RESENDABLE,
                                                                                   new MessageId(messageId),
                                                                                   dataMessage,
                                                                                   true,
                                                                                   isForStory,
                                                                                   null,
                                                                                   null);

    if (conversationRecipient.getId().equals(Recipient.self().getId())) {
      AppDependencies.getSignalServiceMessageSender().sendSyncMessage(dataMessage);
    }

    return GroupSendJobHelper.getCompletedSends(destinations, results);
  }

  public static class Factory implements Job.Factory<RemoteDeleteSendJob> {

    @Override
    public @NonNull RemoteDeleteSendJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      JsonJobData data = JsonJobData.deserialize(serializedData);

      long              messageId             = data.getLong(KEY_MESSAGE_ID);
      List<RecipientId> recipients            = RecipientId.fromSerializedList(data.getString(KEY_RECIPIENTS));
      int               initialRecipientCount = data.getInt(KEY_INITIAL_RECIPIENT_COUNT);

      return new RemoteDeleteSendJob(messageId,  recipients, initialRecipientCount, parameters);
    }
  }
}