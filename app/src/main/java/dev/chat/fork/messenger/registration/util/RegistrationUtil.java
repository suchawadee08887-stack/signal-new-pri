/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.registration.util;

import org.signal.core.util.logging.Log;
import dev.chat.fork.messenger.backup.v2.BackupRepository;
import dev.chat.fork.messenger.backup.v2.MessageBackupTier;
import dev.chat.fork.messenger.dependencies.AppDependencies;
import dev.chat.fork.messenger.jobs.ArchiveBackupIdReservationJob;
import dev.chat.fork.messenger.jobs.DirectoryRefreshJob;
import dev.chat.fork.messenger.jobs.EmojiSearchIndexDownloadJob;
import dev.chat.fork.messenger.jobs.PostRegistrationBackupRedemptionJob;
import dev.chat.fork.messenger.jobs.RefreshAttributesJob;
import dev.chat.fork.messenger.jobs.StorageSyncJob;
import dev.chat.fork.messenger.keyvalue.PhoneNumberPrivacyValues.PhoneNumberDiscoverabilityMode;
import dev.chat.fork.messenger.keyvalue.RestoreDecisionStateUtil;
import dev.chat.fork.messenger.keyvalue.SignalStore;
import dev.chat.fork.messenger.recipients.Recipient;
import dev.chat.fork.messenger.util.RemoteConfig;

public final class RegistrationUtil {

  private static final String TAG = Log.tag(RegistrationUtil.class);

  private RegistrationUtil() {}

  /**
   * There's several events where a registration may or may not be considered complete based on what
   * path a user has taken. This will only truly mark registration as complete if all of the
   * requirements are met.
   */
  public static void maybeMarkRegistrationComplete() {
    if (!SignalStore.registration().isRegistrationComplete() &&
        SignalStore.account().isRegistered() &&
        !Recipient.self().getProfileName().isEmpty() &&
        (SignalStore.svr().hasPin() || SignalStore.svr().hasOptedOut() || SignalStore.account().isLinkedDevice()) &&
        RestoreDecisionStateUtil.isTerminal(SignalStore.registration().getRestoreDecisionState()))
    {
      Log.i(TAG, "Marking registration completed.", new Throwable());
      SignalStore.registration().markRegistrationComplete();
      SignalStore.registration().setLocalRegistrationMetadata(null);
      SignalStore.registration().setRestoreMethodToken(null);

      if (SignalStore.phoneNumberPrivacy().getPhoneNumberDiscoverabilityMode() == PhoneNumberDiscoverabilityMode.UNDECIDED) {
        Log.w(TAG, "Phone number discoverability mode is still UNDECIDED. Setting to DISCOVERABLE.");
        SignalStore.phoneNumberPrivacy().setPhoneNumberDiscoverabilityMode(PhoneNumberDiscoverabilityMode.DISCOVERABLE);
      }

      AppDependencies.getJobManager().startChain(new RefreshAttributesJob())
                     .then(StorageSyncJob.forRemoteChange())
                     .then(new DirectoryRefreshJob(false))
                     .enqueue();

      SignalStore.emoji().clearSearchIndexMetadata();
      EmojiSearchIndexDownloadJob.scheduleImmediately();


      BackupRepository.INSTANCE.resetInitializedStateAndAuthCredentials();
      AppDependencies.getJobManager().add(new ArchiveBackupIdReservationJob());
      AppDependencies.getJobManager().add(new PostRegistrationBackupRedemptionJob());

    } else if (!SignalStore.registration().isRegistrationComplete()) {
      Log.i(TAG, "Registration is not yet complete.", new Throwable());
    }
  }
}
