package dev.chat.fork.messenger.jobs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.signal.core.util.logging.Log;
import dev.chat.fork.messenger.dependencies.AppDependencies;
import dev.chat.fork.messenger.jobmanager.Job;
import dev.chat.fork.messenger.jobmanager.impl.NetworkConstraint;
import dev.chat.fork.messenger.jobmanager.impl.SealedSenderConstraint;
import dev.chat.fork.messenger.keyvalue.SignalStore;
import dev.chat.fork.messenger.net.NotPushRegisteredException;
import dev.chat.fork.messenger.recipients.Recipient;
import dev.chat.fork.messenger.util.TextSecurePreferences;
import org.whispersystems.signalservice.api.SignalServiceMessageSender;
import org.whispersystems.signalservice.api.messages.multidevice.SignalServiceSyncMessage;
import org.signal.network.exceptions.PushNetworkException;
import org.whispersystems.signalservice.api.push.exceptions.ServerRejectedException;

public class MultiDeviceStorageSyncRequestJob extends BaseJob {

  public static final String KEY = "MultiDeviceStorageSyncRequestJob";

  private static final String TAG = Log.tag(MultiDeviceStorageSyncRequestJob.class);

  public MultiDeviceStorageSyncRequestJob() {
    this(new Parameters.Builder()
                       .setQueue("MultiDeviceStorageSyncRequestJob")
                       .setMaxInstancesForFactory(2)
                       .addConstraint(NetworkConstraint.KEY)
                       .addConstraint(SealedSenderConstraint.KEY)
                       .setMaxAttempts(10)
                       .build());
  }

  private MultiDeviceStorageSyncRequestJob(@NonNull Parameters parameters) {
    super(parameters);
  }

  @Override
  public @Nullable byte[] serialize() {
    return null;
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

    if (!SignalStore.account().isMultiDevice()) {
      Log.i(TAG, "Not multi device, aborting...");
      return;
    }

    SignalServiceMessageSender messageSender = AppDependencies.getSignalServiceMessageSender();

    messageSender.sendSyncMessage(SignalServiceSyncMessage.forFetchLatest(SignalServiceSyncMessage.FetchType.STORAGE_MANIFEST));
  }

  @Override
  protected boolean onShouldRetry(@NonNull Exception e) {
    if (e instanceof ServerRejectedException) return false;
    return e instanceof PushNetworkException;
  }

  @Override
  public void onFailure() {
    Log.w(TAG, "Did not succeed!");
  }

  public static final class Factory implements Job.Factory<MultiDeviceStorageSyncRequestJob> {
    @Override
    public @NonNull MultiDeviceStorageSyncRequestJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      return new MultiDeviceStorageSyncRequestJob(parameters);
    }
  }
}
