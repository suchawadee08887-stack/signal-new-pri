package dev.chat.fork.messenger.jobs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.signal.core.util.Hex;
import org.signal.core.util.logging.Log;
import dev.chat.fork.messenger.database.SignalDatabase;
import dev.chat.fork.messenger.database.StickerTables.StickerPackRecordReader;
import dev.chat.fork.messenger.database.model.StickerPackRecord;
import dev.chat.fork.messenger.dependencies.AppDependencies;
import dev.chat.fork.messenger.jobmanager.Job;
import dev.chat.fork.messenger.jobmanager.impl.NetworkConstraint;
import dev.chat.fork.messenger.jobmanager.impl.SealedSenderConstraint;
import dev.chat.fork.messenger.keyvalue.SignalStore;
import dev.chat.fork.messenger.net.NotPushRegisteredException;
import dev.chat.fork.messenger.recipients.Recipient;
import org.whispersystems.signalservice.api.SignalServiceMessageSender;
import org.whispersystems.signalservice.api.messages.multidevice.SignalServiceSyncMessage;
import org.whispersystems.signalservice.api.messages.multidevice.StickerPackOperationMessage;
import org.signal.network.exceptions.PushNetworkException;
import org.whispersystems.signalservice.api.push.exceptions.ServerRejectedException;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Tells a linked desktop about all installed sticker packs.
 */
public class MultiDeviceStickerPackSyncJob extends BaseJob {

  private static final String TAG = Log.tag(MultiDeviceStickerPackSyncJob.class);

  public static final String KEY = "MultiDeviceStickerPackSyncJob";

  public MultiDeviceStickerPackSyncJob() {
    this(new Parameters.Builder()
                           .setQueue("MultiDeviceStickerPackSyncJob")
                           .addConstraint(NetworkConstraint.KEY)
                           .addConstraint(SealedSenderConstraint.KEY)
                           .setLifespan(TimeUnit.DAYS.toMillis(1))
                           .build());
  }

  public MultiDeviceStickerPackSyncJob(@NonNull Parameters parameters) {
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

    List<StickerPackOperationMessage> operations = new LinkedList<>();

    try (StickerPackRecordReader reader = new StickerPackRecordReader(SignalDatabase.stickers().getInstalledStickerPacks())) {
      StickerPackRecord pack;
      while ((pack = reader.getNext()) != null) {
        byte[] packIdBytes  = Hex.fromStringCondensed(pack.packId);
        byte[] packKeyBytes = Hex.fromStringCondensed(pack.packKey);

        operations.add(new StickerPackOperationMessage(packIdBytes, packKeyBytes, StickerPackOperationMessage.Type.INSTALL));
      }
    }

    SignalServiceMessageSender messageSender = AppDependencies.getSignalServiceMessageSender();
    messageSender.sendSyncMessage(SignalServiceSyncMessage.forStickerPackOperations(operations)
    );
  }

  @Override
  protected boolean onShouldRetry(@NonNull Exception e) {
    if (e instanceof ServerRejectedException) return false;
    return e instanceof PushNetworkException;
  }

  @Override
  public void onFailure() {
    Log.w(TAG, "Failed to sync sticker pack operation!");
  }

  public static class Factory implements Job.Factory<MultiDeviceStickerPackSyncJob> {

    @Override
    public @NonNull
    MultiDeviceStickerPackSyncJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      return new MultiDeviceStickerPackSyncJob(parameters);
    }
  }
}
