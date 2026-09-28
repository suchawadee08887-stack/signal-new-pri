/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.registration.ui.restore

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.signal.core.util.Stopwatch
import org.signal.core.util.logging.Log
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.jobmanager.enqueueBlocking
import dev.chat.fork.messenger.jobmanager.runJobBlocking
import dev.chat.fork.messenger.jobs.ProfileUploadJob
import dev.chat.fork.messenger.jobs.ReclaimUsernameAndLinkJob
import dev.chat.fork.messenger.jobs.StorageAccountRestoreJob
import dev.chat.fork.messenger.jobs.StorageSyncJob
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.registration.data.RegistrationRepository
import dev.chat.fork.messenger.registration.util.RegistrationUtil
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object StorageServiceRestore {
  private val TAG = Log.tag(StorageServiceRestore::class)

  /**
   * Restore account data from Storage Service in a quasi-blocking manner. Uses existing jobs
   * to perform the restore but will not wait indefinitely for them to finish so may return prior
   * to completing the restore.
   */
  suspend fun restore() {
    withContext(Dispatchers.IO) {
      val stopwatch = Stopwatch("storage-service-restore")

      SignalStore.storageService.needsAccountRestore = false

      AppDependencies.jobManager.runJobBlocking(StorageAccountRestoreJob(), StorageAccountRestoreJob.LIFESPAN.milliseconds)
      stopwatch.split("account-restore")

      AppDependencies
        .jobManager
        .startChain(StorageSyncJob.forAccountRestore())
        .then(ReclaimUsernameAndLinkJob())
        .enqueueBlocking(10.seconds)
      stopwatch.split("storage-sync-restore")

      stopwatch.stop(TAG)

      val isMissingProfileData = RegistrationRepository.isMissingProfileData()

      RegistrationUtil.maybeMarkRegistrationComplete()
      if (!isMissingProfileData && SignalStore.account.isPrimaryDevice) {
        AppDependencies.jobManager.add(ProfileUploadJob())
      }
    }
  }
}
