/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.jobmanager.migrations

import dev.chat.fork.messenger.jobmanager.JobMigration

/**
 * Used as a replacement for another JobMigration that is no longer necessary.
 */
class DeprecatedJobMigration(version: Int) : JobMigration(version) {
  override fun migrate(jobData: JobData): JobData = jobData
}
