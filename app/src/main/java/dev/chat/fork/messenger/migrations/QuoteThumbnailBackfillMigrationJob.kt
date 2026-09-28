/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.migrations

import org.signal.core.util.logging.Log
import org.signal.core.util.update
import dev.chat.fork.messenger.database.AttachmentTable
import dev.chat.fork.messenger.database.AttachmentTable.Companion.DATA_FILE
import dev.chat.fork.messenger.database.AttachmentTable.Companion.QUOTE
import dev.chat.fork.messenger.database.AttachmentTable.Companion.QUOTE_PENDING_TRANSCODE
import dev.chat.fork.messenger.database.AttachmentTable.Companion.TABLE_NAME
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.jobmanager.Job
import dev.chat.fork.messenger.jobs.QuoteThumbnailBackfillJob
import dev.chat.fork.messenger.keyvalue.SignalStore
import java.lang.Exception

/**
 * Kicks off the quote attachment thumbnail generation process by marking quote attachments
 * for processing and enqueueing a [QuoteThumbnailBackfillJob].
 */
internal class QuoteThumbnailBackfillMigrationJob(parameters: Parameters = Parameters.Builder().build()) : MigrationJob(parameters) {

  companion object {
    val TAG = Log.tag(QuoteThumbnailBackfillMigrationJob::class.java)
    const val KEY = "QuoteThumbnailBackfillMigrationJob"
  }

  override fun getFactoryKey(): String = KEY

  override fun isUiBlocking(): Boolean = false

  override fun performMigration() {
    val markedCount = SignalDatabase.attachments.migrationMarkQuoteAttachmentsForThumbnailProcessing()
    SignalStore.misc.startedQuoteThumbnailMigration = true

    Log.i(TAG, "Marked $markedCount quote attachments for thumbnail processing")

    if (markedCount > 0) {
      AppDependencies.jobManager.add(QuoteThumbnailBackfillJob())
    } else {
      Log.i(TAG, "No quote attachments to process.")
    }
  }

  override fun shouldRetry(e: Exception): Boolean = false

  private fun AttachmentTable.migrationMarkQuoteAttachmentsForThumbnailProcessing(): Int {
    return writableDatabase
      .update(TABLE_NAME)
      .values(QUOTE to QUOTE_PENDING_TRANSCODE)
      .where("$QUOTE != 0 AND $DATA_FILE NOT NULL")
      .run()
  }

  class Factory : Job.Factory<QuoteThumbnailBackfillMigrationJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): QuoteThumbnailBackfillMigrationJob {
      return QuoteThumbnailBackfillMigrationJob(parameters)
    }
  }
}
