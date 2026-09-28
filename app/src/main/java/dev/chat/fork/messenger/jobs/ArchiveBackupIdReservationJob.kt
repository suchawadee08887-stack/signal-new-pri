/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.jobs

import arrow.core.Either
import org.signal.core.util.logging.Log
import org.signal.network.service.ArchiveError
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.jobmanager.CoroutineJob
import dev.chat.fork.messenger.jobmanager.Job
import dev.chat.fork.messenger.jobmanager.impl.NetworkConstraint
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.util.RemoteConfig
import dev.chat.fork.messenger.util.TextSecurePreferences

/**
 * Reserves backupIds for both text+media. The intention is that every registered user should be doing this, so it should happen post-registration
 * (as well as in a migration for pre-existing users).
 *
 * Calling this repeatedly is a no-op from the server's perspective, so no need to be careful around retries or anything.
 */
class ArchiveBackupIdReservationJob private constructor(parameters: Parameters) : CoroutineJob(parameters) {

  companion object {
    private val TAG = Log.tag(ArchiveBackupIdReservationJob::class)

    const val KEY = "ArchiveBackupIdReservationJob"
  }

  constructor() : this(
    Parameters.Builder()
      .setQueue("ArchiveBackupIdReservationJob")
      .addConstraint(NetworkConstraint.KEY)
      .setLifespan(Parameters.IMMORTAL)
      .setMaxAttempts(Parameters.UNLIMITED)
      .build()
  )

  override fun serialize(): ByteArray? = null

  override fun getFactoryKey(): String = KEY

  override suspend fun doRun(): Result {
    if (!SignalStore.account.isRegistered) {
      Log.w(TAG, "Not registered. Skipping.")
      return Result.success()
    }

    if (TextSecurePreferences.isUnauthorizedReceived(context)) {
      Log.w(TAG, "Not authorized. Skipping.")
      return Result.success()
    }

    if (SignalStore.account.isLinkedDevice) {
      Log.i(TAG, "Linked device. Skipping.")
      return Result.success()
    }

    return when (val result = AppDependencies.archiveService.triggerBackupIdReservation()) {
      is Either.Right -> Result.success()
      is Either.Left -> when (val error = result.value) {
        is ArchiveError.NetworkError -> {
          if (error.isServerSide) {
            Log.w(TAG, "Server error while reserving backupId. Backing off hard.", error.exception)
            Result.retry(RemoteConfig.serverErrorMaxBackoff)
          } else {
            Result.retry(defaultBackoff())
          }
        }
        is ArchiveError.ApplicationError -> Result.fatalFailure(RuntimeException(error.exception))
        is ArchiveError.CredentialError.RateLimited -> Result.retry(error.retryAfter?.inWholeMilliseconds ?: defaultBackoff())
        is ArchiveError.CredentialError.InvalidRequest,
        is ArchiveError.CredentialError.Unauthorized,
        is ArchiveError.CredentialError.NotFound,
        is ArchiveError.CredentialError.ZkVerificationFailed -> {
          Log.w(TAG, "Failed to reserve backupId: ${error::class.simpleName}. This should only happen on a malformed request. Reducing backoff interval to be safe.")
          Result.retry(RemoteConfig.serverErrorMaxBackoff)
        }
      }
    }
  }

  override fun onFailure() = Unit

  class Factory : Job.Factory<ArchiveBackupIdReservationJob> {
    override fun create(parameters: Parameters, data: ByteArray?): ArchiveBackupIdReservationJob {
      return ArchiveBackupIdReservationJob(parameters)
    }
  }
}
