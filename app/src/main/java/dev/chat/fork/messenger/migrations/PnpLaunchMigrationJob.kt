/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.migrations

import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.jobmanager.Job
import dev.chat.fork.messenger.jobs.ProfileUploadJob
import dev.chat.fork.messenger.jobs.RefreshAttributesJob
import java.lang.Exception

/**
 * Kicks off a chain of jobs to update the server with our latest PNP settings.
 */
internal class PnpLaunchMigrationJob(parameters: Parameters = Parameters.Builder().build()) : MigrationJob(parameters) {
  companion object {
    const val KEY = "PnpLaunchMigrationJob"
  }

  override fun getFactoryKey(): String = KEY

  override fun isUiBlocking(): Boolean = false

  override fun performMigration() {
    AppDependencies.jobManager
      .startChain(RefreshAttributesJob())
      .then(ProfileUploadJob())
      .enqueue()
  }

  override fun shouldRetry(e: Exception): Boolean = false

  class Factory : Job.Factory<PnpLaunchMigrationJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): PnpLaunchMigrationJob {
      return PnpLaunchMigrationJob(parameters)
    }
  }
}
