package dev.chat.fork.messenger.jobs;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import org.signal.core.util.logging.Log;
import org.signal.libsignal.protocol.NoSessionException;
import dev.chat.fork.messenger.database.GroupTable;
import dev.chat.fork.messenger.database.NoSuchMessageException;
import dev.chat.fork.messenger.database.ReactionTable;
import dev.chat.fork.messenger.database.SignalDatabase;
import dev.chat.fork.messenger.database.model.MessageId;
import dev.chat.fork.messenger.database.model.MessageRecord;
import dev.chat.fork.messenger.database.model.ReactionRecord;
import dev.chat.fork.messenger.database.model.RecipientRecord;
import dev.chat.fork.messenger.dependencies.AppDependencies;
import dev.chat.fork.messenger.jobmanager.Job;
import dev.chat.fork.messenger.jobmanager.JsonJobData;
import dev.chat.fork.messenger.jobmanager.impl.NetworkConstraint;
import dev.chat.fork.messenger.jobmanager.impl.SealedSenderConstraint;
import dev.chat.fork.messenger.keyvalue.SignalStore;
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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ReactionSendJob extends BaseJob {

  public static final String KEY = "ReactionSendJob";

  private static final String TAG = Log.tag(ReactionSendJob.class);

  private static final String KEY_MESSAGE_ID              = "message_id";
  private static final String KEY_REACTION_EMOJI          = "reaction_emoji";
  private static final String KEY_REACTION_AUTHOR         = "reaction_author";
  private static final String KEY_REACTION_DATE_SENT      = "reaction_date_sent";
  private static final String KEY_REACTION_DATE_RECEIVED  = "reaction_date_received";
  private static final String KEY_REMOVE                  = "remove";
  private static final String KEY_RECIPIENTS              = "recipients";
  private static final String KEY_INITIAL_RECIPIENT_COUNT = "initial_recipient_count";

  private final MessageId         messageId;
  private final List<RecipientId> recipients;
  private final int               initialRecipientCount;
  private final ReactionRecord    reaction;
  private final boolean           remove;


  @WorkerThread
  public static @NonNull ReactionSendJob create(@NonNull Context context,
                                                @NonNull MessageId messageId,
                                                @NonNull ReactionRecord reaction,
                                                boolean remove)
      throws NoSuchMessageException
  {
    MessageRecord message = SignalDatabase.messages().getMessageRecord(messageId.getId());

    RecipientId conversationRecipientId = SignalDatabase.threads().getRecipientIdForThreadId(message.getThreadId());

    if (conversationRecipientId == null) {
      throw new AssertionError("We have a message, but couldn't find the thread!");
    }

    RecipientRecord conversationRecipient = SignalDatabase.recipients().getRecord(conversationRecipientId);

    List<RecipientId> recipients = conversationRecipient.getGroupId() != null ? RecipientUtil.getEligibleForSending(Recipient.resolvedList(SignalDatabase.groups().getGroupMemberIds(conversationRecipient.getGroupId(), GroupTable.MemberSet.FULL_MEMBERS_EXCLUDING_SELF)))
                                                                                            .stream()
                                                                                            .map(Recipient::getId)
                                                                                            .collect(Collectors.toList())
                                                                             : Collections.singletonList(conversationRecipient.getId());

    return new ReactionSendJob(messageId,
                               recipients,
                               recipients.size(),
                               reaction,
                               remove,
                               new Parameters.Builder()
                                             .setQueue(conversationRecipient.getId().toQueueKey())
                                             .addConstraint(NetworkConstraint.KEY)
                                             .addConstraint(SealedSenderConstraint.KEY)
                                             .setLifespan(TimeUnit.DAYS.toMillis(1))
                                             .setMaxAttempts(Parameters.UNLIMITED)
                                             .build());
  }

  private ReactionSendJob(@NonNull MessageId messageId,
                          @NonNull List<RecipientId> recipients,
                          int initialRecipientCount,
                          @NonNull ReactionRecord reaction,
                          boolean remove,
                          @NonNull Parameters parameters)
  {
    super(parameters);

    this.messageId             = messageId;
    this.recipients            = recipients;
    this.initialRecipientCount = initialRecipientCount;
    this.reaction              = reaction;
    this.remove                = remove;
  }

  @Override
  public @Nullable byte[] serialize() {
    return new JsonJobData.Builder().putLong(KEY_MESSAGE_ID, messageId.getId())
                                    .putString(KEY_REACTION_EMOJI, reaction.getEmoji())
                                    .putString(KEY_REACTION_AUTHOR, reaction.getAuthor().serialize())
                                    .putLong(KEY_REACTION_DATE_SENT, reaction.getDateSent())
                                    .putLong(KEY_REACTION_DATE_RECEIVED, reaction.getDateReceived())
                                    .putBoolean(KEY_REMOVE, remove)
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

    ReactionTable reactionTable = SignalDatabase.reactions();
    MessageRecord message       = SignalDatabase.messages().getMessageRecord(messageId.getId());

    Recipient targetAuthor        = message.getFromRecipient();
    long      targetSentTimestamp = message.getDateSent();

    if (targetAuthor.getId().equals(SignalStore.releaseChannel().getReleaseChannelRecipientId())) {
      return;
    }

    if (!remove && !reactionTable.hasReaction(messageId, reaction)) {
      Log.w(TAG, "Went to add a reaction, but it's no longer present on the message!");
      return;
    }

    if (remove && reactionTable.hasReaction(messageId, reaction)) {
      Log.w(TAG, "Went to remove a reaction, but it's still there!");
      return;
    }

    RecipientId conversationRecipientId = SignalDatabase.threads().getRecipientIdForThreadId(message.getThreadId());

    if (conversationRecipientId == null) {
      throw new AssertionError("We have a message, but couldn't find the thread!");
    }

    RecipientRecord conversationRecipient = SignalDatabase.recipients().getRecord(conversationRecipientId);

    if (conversationRecipient.getGroupId() != null && (conversationRecipient.getGroupId().isV1() || conversationRecipient.getGroupId().isMms())) {
      Log.w(TAG, "Cannot send reactions to legacy groups.");
      return;
    }

    if (conversationRecipient.getGroupId() != null && conversationRecipient.getGroupId().isV2() && !SignalDatabase.groups().isActive(conversationRecipient.getGroupId())) {
      Log.w(TAG, "Cannot send reactions to terminated or inactive groups.");
      return;
    }

    List<Recipient>   resolved     = recipients.stream().map(Recipient::resolved).collect(Collectors.toList());
    List<RecipientId> unregistered = resolved.stream().filter(Recipient::isUnregistered).map(Recipient::getId).collect(Collectors.toList());
    List<Recipient>   destinations = resolved.stream().filter(Recipient::isMaybeRegistered).collect(Collectors.toList());
    List<Recipient>   completions  = deliver(conversationRecipient, destinations, targetAuthor, targetSentTimestamp);

    recipients.removeAll(unregistered);
    recipients.removeAll(completions.stream().map(Recipient::getId).collect(Collectors.toList()));

    Log.i(TAG, "Completed now: " + completions.size() + ", Remaining: " + recipients.size());

    if (!recipients.isEmpty()) {
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
    if (recipients.size() < initialRecipientCount) {
      Log.w(TAG, "Only sent a reaction to " + recipients.size() + "/" + initialRecipientCount + " recipients. Still, it sent to someone, so it stays.");
      return;
    }

    Log.w(TAG, "Failed to send the reaction to all recipients!");

    ReactionTable reactionTable = SignalDatabase.reactions();

    if (remove && !reactionTable.hasReaction(messageId, reaction)) {
      Log.w(TAG, "Reaction removal failed, so adding the reaction back.");
      reactionTable.addReaction(messageId, reaction);
    } else if (!remove && reactionTable.hasReaction(messageId, reaction)){
      Log.w(TAG, "Reaction addition failed, so removing the reaction.");
      reactionTable.deleteReaction(messageId, reaction.getAuthor());
    } else {
      Log.w(TAG, "Reaction state didn't match what we'd expect to revert it, so we're just leaving it alone.");
    }
  }

  private @NonNull List<Recipient> deliver(@NonNull RecipientRecord conversationRecipient, @NonNull List<Recipient> destinations, @NonNull Recipient targetAuthor, long targetSentTimestamp)
      throws IOException, UntrustedIdentityException, NoSessionException, UndeliverableMessageException
  {
    SignalServiceDataMessage.Builder dataMessageBuilder = SignalServiceDataMessage.newBuilder()
                                                                                  .withTimestamp(System.currentTimeMillis())
                                                                                  .withReaction(buildReaction(reaction, remove, targetAuthor, targetSentTimestamp));

    if (conversationRecipient.getGroupId() != null) {
      GroupUtil.setDataMessageGroupContext(context, dataMessageBuilder, conversationRecipient.getGroupId().requirePush());
    }

    SignalServiceDataMessage dataMessage         = dataMessageBuilder.build();
    List<Recipient>          nonSelfDestinations = destinations.stream().filter(r -> !r.isSelf()).collect(Collectors.toList());
    boolean                  includesSelf        = nonSelfDestinations.size() != destinations.size();
    List<SendMessageResult>  results             = GroupSendUtil.sendResendableDataMessage(context,
                                                                                           conversationRecipient.getGroupId() != null ? conversationRecipient.getGroupId().requireV2() : null,
                                                                                           null,
                                                                                           nonSelfDestinations,
                                                                                           false,
                                                                                           ContentHint.RESENDABLE,
                                                                                           messageId,
                                                                                           dataMessage,
                                                                                           true,
                                                                                           false,
                                                                                           null,
                                                                                           null);

    if (includesSelf) {
      results.add(AppDependencies.getSignalServiceMessageSender().sendSyncMessage(dataMessage));
    }

    GroupSendJobHelper.SendResult groupResult = GroupSendJobHelper.getCompletedSends(destinations, results);

    for (RecipientId unregistered : groupResult.unregistered) {
      SignalDatabase.recipients().markUnregistered(unregistered);
    }

    return groupResult.completed;
  }

  private static SignalServiceDataMessage.Reaction buildReaction(@NonNull ReactionRecord reaction,
                                                                 boolean remove,
                                                                 @NonNull Recipient targetAuthor,
                                                                 long targetSentTimestamp)
  {
    return new SignalServiceDataMessage.Reaction(reaction.getEmoji(),
                                                 remove,
                                                 targetAuthor.requireServiceId(),
                                                 targetSentTimestamp);
  }

  public static class Factory implements Job.Factory<ReactionSendJob> {

    @Override
    public @NonNull
    ReactionSendJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      JsonJobData data = JsonJobData.deserialize(serializedData);

      long              messageId             = data.getLong(KEY_MESSAGE_ID);
      List<RecipientId> recipients            = RecipientId.fromSerializedList(data.getString(KEY_RECIPIENTS));
      int               initialRecipientCount = data.getInt(KEY_INITIAL_RECIPIENT_COUNT);
      ReactionRecord    reaction              = new ReactionRecord(data.getString(KEY_REACTION_EMOJI),
                                                                   RecipientId.from(data.getString(KEY_REACTION_AUTHOR)),
                                                                   data.getLong(KEY_REACTION_DATE_SENT),
                                                                   data.getLong(KEY_REACTION_DATE_RECEIVED));
      boolean           remove                = data.getBoolean(KEY_REMOVE);

      return new ReactionSendJob(new MessageId(messageId), recipients, initialRecipientCount, reaction, remove, parameters);
    }
  }
}
