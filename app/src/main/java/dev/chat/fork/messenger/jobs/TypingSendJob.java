package dev.chat.fork.messenger.jobs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.stream.Collectors;

import org.signal.core.util.logging.Log;
import dev.chat.fork.messenger.database.GroupTable;
import dev.chat.fork.messenger.database.SignalDatabase;
import dev.chat.fork.messenger.groups.GroupId;
import dev.chat.fork.messenger.jobmanager.JsonJobData;
import dev.chat.fork.messenger.jobmanager.Job;
import dev.chat.fork.messenger.jobmanager.impl.NetworkConstraint;
import dev.chat.fork.messenger.jobmanager.impl.SealedSenderConstraint;
import dev.chat.fork.messenger.messages.GroupSendUtil;
import dev.chat.fork.messenger.net.NotPushRegisteredException;
import dev.chat.fork.messenger.recipients.Recipient;
import dev.chat.fork.messenger.recipients.RecipientUtil;
import dev.chat.fork.messenger.util.TextSecurePreferences;
import org.whispersystems.signalservice.api.messages.SignalServiceTypingMessage;
import org.whispersystems.signalservice.api.messages.SignalServiceTypingMessage.Action;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class TypingSendJob extends BaseJob {

  public static final String KEY = "TypingSendJob";

  private static final String TAG = Log.tag(TypingSendJob.class);

  private static final String KEY_THREAD_ID = "thread_id";
  private static final String KEY_TYPING    = "typing";

  private long    threadId;
  private boolean typing;

  public TypingSendJob(long threadId, boolean typing) {
    this(new Job.Parameters.Builder()
                           .setQueue(getQueue(threadId))
                           .setMaxAttempts(1)
                           .setLifespan(TimeUnit.SECONDS.toMillis(5))
                           .addConstraint(NetworkConstraint.KEY)
                           .addConstraint(SealedSenderConstraint.KEY)
                           .setMemoryOnly(true)
                           .build(),
         threadId,
         typing);
  }

  public static String getQueue(long threadId) {
    return "TYPING_" + threadId;
  }

  private TypingSendJob(@NonNull Job.Parameters parameters, long threadId, boolean typing) {
    super(parameters);

    this.threadId = threadId;
    this.typing   = typing;
  }


  @Override
  public @Nullable byte[] serialize() {
    return new JsonJobData.Builder().putLong(KEY_THREAD_ID, threadId)
                                    .putBoolean(KEY_TYPING, typing)
                                    .serialize();
  }

  @Override
  public @NonNull String getFactoryKey() {
    return KEY;
  }

  @Override
  public void onRun() throws Exception {
    if (!Recipient.self().isRegistered()) {
      throw new NotPushRegisteredException();
    }

    if (!TextSecurePreferences.isTypingIndicatorsEnabled(context)) {
      return;
    }

    Log.d(TAG, "Sending typing " + (typing ? "started" : "stopped") + " for thread " + threadId);

    Recipient recipient = SignalDatabase.threads().getRecipientForThreadId(threadId);

    if (recipient == null) {
      Log.w(TAG, "Tried to send a typing indicator to a non-existent thread.");
      return;
    }

    if (recipient.isBlocked()) {
      Log.w(TAG, "Not sending typing indicators to blocked recipients.");
      return;
    }

    if (recipient.isSelf()) {
      Log.w(TAG, "Not sending typing indicators to self.");
      return;
    }

    if (recipient.isPushV1Group() || recipient.isMmsGroup()) {
      Log.w(TAG, "Not sending typing indicators to unsupported groups.");
      return;
    }

    if (recipient.isPushV2Group() && !SignalDatabase.groups().isActive(recipient.requireGroupId())) {
      Log.w(TAG, "Not sending typing indicators to terminated or inactive groups.");
      return;
    }

    if (!recipient.isRegistered()) {
      Log.w(TAG, "Not sending typing indicators to non-Signal recipients.");
      return;
    }

    List<Recipient>  recipients = Collections.singletonList(recipient);
    Optional<byte[]> groupId    = Optional.empty();

    if (recipient.isGroup()) {
      recipients = SignalDatabase.groups().getGroupMembers(recipient.requireGroupId(), GroupTable.MemberSet.FULL_MEMBERS_EXCLUDING_SELF);
      groupId    = Optional.of(recipient.requireGroupId().getDecodedId());
    }

    recipients = RecipientUtil.getEligibleForSending(recipients.stream()
                                                               .map(Recipient::resolve).collect(Collectors.toList()));

    SignalServiceTypingMessage typingMessage = new SignalServiceTypingMessage(typing ? Action.STARTED : Action.STOPPED, System.currentTimeMillis(), groupId);

    GroupSendUtil.sendTypingMessage(context,
                                    recipient.getGroupId().map(GroupId::requireV2).orElse(null),
                                    recipients,
                                    typingMessage,
                                    this::isCanceled);
  }

  @Override
  public void onFailure() {
  }

  @Override
  protected boolean onShouldRetry(@NonNull Exception exception) {
    return false;
  }

  public static final class Factory implements Job.Factory<TypingSendJob> {
    @Override
    public @NonNull TypingSendJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      JsonJobData data = JsonJobData.deserialize(serializedData);
      return new TypingSendJob(parameters, data.getLong(KEY_THREAD_ID), data.getBoolean(KEY_TYPING));
    }
  }
}
