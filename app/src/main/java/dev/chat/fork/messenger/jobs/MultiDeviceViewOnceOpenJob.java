package dev.chat.fork.messenger.jobs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.signal.core.util.JsonUtils;
import org.signal.core.util.logging.Log;
import org.signal.libsignal.protocol.NoSessionException;
import org.signal.network.exceptions.PushNetworkException;
import dev.chat.fork.messenger.database.MessageTable.SyncMessageId;
import dev.chat.fork.messenger.database.RecipientTable.RegisteredState;
import dev.chat.fork.messenger.database.SignalDatabase;
import dev.chat.fork.messenger.database.model.RecipientRecord;
import dev.chat.fork.messenger.dependencies.AppDependencies;
import dev.chat.fork.messenger.jobmanager.Job;
import dev.chat.fork.messenger.jobmanager.JsonJobData;
import dev.chat.fork.messenger.jobmanager.impl.NetworkConstraint;
import dev.chat.fork.messenger.jobmanager.impl.SealedSenderConstraint;
import dev.chat.fork.messenger.keyvalue.SignalStore;
import dev.chat.fork.messenger.net.NotPushRegisteredException;
import dev.chat.fork.messenger.recipients.Recipient;
import dev.chat.fork.messenger.recipients.RecipientId;
import org.whispersystems.signalservice.api.SignalServiceMessageSender;
import org.whispersystems.signalservice.api.crypto.UntrustedIdentityException;
import org.whispersystems.signalservice.api.messages.multidevice.SignalServiceSyncMessage;
import org.whispersystems.signalservice.api.messages.multidevice.ViewOnceOpenMessage;
import org.whispersystems.signalservice.api.push.exceptions.ServerRejectedException;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class MultiDeviceViewOnceOpenJob extends BaseJob {

  public static final String KEY = "MultiDeviceRevealUpdateJob";

  private static final String TAG = Log.tag(MultiDeviceViewOnceOpenJob.class);

  private static final String KEY_MESSAGE_ID = "message_id";

  private SerializableSyncMessageId messageId;

  public MultiDeviceViewOnceOpenJob(SyncMessageId messageId) {
    this(new Parameters.Builder()
                       .addConstraint(NetworkConstraint.KEY)
                       .addConstraint(SealedSenderConstraint.KEY)
                       .setLifespan(TimeUnit.DAYS.toMillis(1))
                       .setMaxAttempts(Parameters.UNLIMITED)
                       .build(),
         messageId);
  }

  private MultiDeviceViewOnceOpenJob(@NonNull Parameters parameters, @NonNull SyncMessageId syncMessageId) {
    super(parameters);
    this.messageId = new SerializableSyncMessageId(syncMessageId.getRecipientId().serialize(), syncMessageId.getTimetamp());
  }

  @Override
  public @Nullable byte[] serialize() {
    String serialized;

    try {
      serialized = JsonUtils.toJson(messageId);
    } catch (IOException e) {
      throw new AssertionError(e);
    }

    return new JsonJobData.Builder().putString(KEY_MESSAGE_ID, serialized).serialize();
  }

  @Override
  public @NonNull String getFactoryKey() {
    return KEY;
  }

  @Override
  public void onRun() throws IOException, UntrustedIdentityException, NoSessionException {
    if (!Recipient.self().isRegistered()) {
      throw new NotPushRegisteredException();
    }

    if (!SignalStore.account().isMultiDevice()) {
      Log.i(TAG, "Not multi device...");
      return;
    }

    SignalServiceMessageSender messageSender = AppDependencies.getSignalServiceMessageSender();
    RecipientRecord            recipient     = SignalDatabase.recipients().getRecord(RecipientId.from(messageId.recipientId));

    if (recipient.getRegistered() == RegisteredState.NOT_REGISTERED || recipient.getServiceId() == null) {
      Log.w(TAG, recipient.getId() + " not registered!");
      return;
    }

    ViewOnceOpenMessage openMessage = new ViewOnceOpenMessage(recipient.getServiceId(), messageId.timestamp);

    messageSender.sendSyncMessage(SignalServiceSyncMessage.forViewOnceOpen(openMessage));
  }

  @Override
  public boolean onShouldRetry(@NonNull Exception exception) {
    if (exception instanceof ServerRejectedException) return false;
    return exception instanceof PushNetworkException;
  }

  @Override
  public void onFailure() {

  }

  private static class SerializableSyncMessageId implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty
    private final String recipientId;

    @JsonProperty
    private final long   timestamp;

    private SerializableSyncMessageId(@JsonProperty("recipientId") String recipientId, @JsonProperty("timestamp") long timestamp) {
      this.recipientId = recipientId;
      this.timestamp   = timestamp;
    }
  }

  public static final class Factory implements Job.Factory<MultiDeviceViewOnceOpenJob> {
    @Override
    public @NonNull MultiDeviceViewOnceOpenJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      JsonJobData data = JsonJobData.deserialize(serializedData);

      SerializableSyncMessageId messageId;
      try {
        messageId = JsonUtils.fromJson(data.getString(KEY_MESSAGE_ID), SerializableSyncMessageId.class);
      } catch (IOException e) {
        throw new AssertionError(e);
      }

      SyncMessageId syncMessageId = new SyncMessageId(RecipientId.from(messageId.recipientId), messageId.timestamp);

      return new MultiDeviceViewOnceOpenJob(parameters, syncMessageId);
    }
  }
}
