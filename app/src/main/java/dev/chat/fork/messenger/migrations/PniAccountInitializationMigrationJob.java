package dev.chat.fork.messenger.migrations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.signal.core.util.logging.Log;
import org.signal.libsignal.protocol.state.PreKeyRecord;
import org.signal.libsignal.protocol.state.SignedPreKeyRecord;
import dev.chat.fork.messenger.crypto.PreKeyUtil;
import dev.chat.fork.messenger.crypto.storage.PreKeyMetadataStore;
import dev.chat.fork.messenger.dependencies.AppDependencies;
import dev.chat.fork.messenger.jobmanager.Job;
import dev.chat.fork.messenger.jobmanager.impl.NetworkConstraint;
import dev.chat.fork.messenger.keyvalue.SignalStore;
import dev.chat.fork.messenger.net.SignalNetwork;
import dev.chat.fork.messenger.recipients.Recipient;
import org.whispersystems.signalservice.api.NetworkResultUtil;
import org.whispersystems.signalservice.api.SignalServiceAccountDataStore;
import org.whispersystems.signalservice.api.account.PreKeyUpload;
import org.signal.core.models.ServiceId.PNI;
import org.whispersystems.signalservice.api.push.ServiceIdType;

import java.io.IOException;
import java.util.List;

/**
 * Initializes various aspects of the PNI identity. Notably:
 * - Creates an identity key
 * - Creates and uploads one-time prekeys
 * - Creates and uploads signed prekeys
 */
public class PniAccountInitializationMigrationJob extends MigrationJob {

  private static final String TAG = Log.tag(PniAccountInitializationMigrationJob.class);

  public static final String KEY = "PniAccountInitializationMigrationJob";

  PniAccountInitializationMigrationJob() {
    this(new Parameters.Builder()
                       .addConstraint(NetworkConstraint.KEY)
                       .build());
  }

  private PniAccountInitializationMigrationJob(@NonNull Parameters parameters) {
    super(parameters);
  }

  @Override
  public boolean isUiBlocking() {
    return false;
  }

  @Override
  public @NonNull String getFactoryKey() {
    return KEY;
  }

  @Override
  public void performMigration() throws IOException {
    if (SignalStore.account().isLinkedDevice()) {
      Log.i(TAG, "Linked device, skipping");
      return;
    }

    PNI pni = SignalStore.account().getPni();

    if (pni == null || SignalStore.account().getAci() == null || !Recipient.self().isRegistered()) {
      Log.w(TAG, "Not yet registered! No need to perform this migration.");
      return;
    }

    if (!SignalStore.account().hasPniIdentityKey()) {
      Log.i(TAG, "Generating PNI identity.");
      SignalStore.account().generatePniIdentityKeyIfNecessary();
    } else {
      Log.w(TAG, "Already generated the PNI identity. Skipping this step.");
    }

    SignalServiceAccountDataStore protocolStore  = AppDependencies.getProtocolStore().pni();
    PreKeyMetadataStore           metadataStore  = SignalStore.account().pniPreKeys();

    if (!metadataStore.isSignedPreKeyRegistered()) {
      Log.i(TAG, "Uploading signed prekey for PNI.");
      SignedPreKeyRecord signedPreKey   = PreKeyUtil.generateAndStoreSignedPreKey(protocolStore, metadataStore);
      List<PreKeyRecord> oneTimePreKeys = PreKeyUtil.generateAndStoreOneTimeEcPreKeys(protocolStore, metadataStore);

      NetworkResultUtil.toPreKeysLegacy(SignalNetwork.keys().setPreKeysSync(new PreKeyUpload(ServiceIdType.PNI, signedPreKey, oneTimePreKeys, null, null)));
      metadataStore.setActiveSignedPreKeyId(signedPreKey.getId());
      metadataStore.setSignedPreKeyRegistered(true);
    } else {
      Log.w(TAG, "Already uploaded signed prekey for PNI. Skipping this step.");
    }
  }

  @Override
  boolean shouldRetry(@NonNull Exception e) {
    return e instanceof IOException;
  }

  public static class Factory implements Job.Factory<PniAccountInitializationMigrationJob> {
    @Override
    public @NonNull PniAccountInitializationMigrationJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      return new PniAccountInitializationMigrationJob(parameters);
    }
  }
}
