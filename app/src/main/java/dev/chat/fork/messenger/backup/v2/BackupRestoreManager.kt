/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.backup.v2

import org.signal.core.models.database.AttachmentId
import org.signal.core.util.concurrent.SignalExecutors
import dev.chat.fork.messenger.attachments.DatabaseAttachment
import dev.chat.fork.messenger.database.AttachmentTable
import dev.chat.fork.messenger.database.model.MessageRecord
import dev.chat.fork.messenger.database.model.MmsMessageRecord
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.jobs.RestoreAttachmentThumbnailJob

/**
 * Responsible for managing logic around restore prioritization
 */
object BackupRestoreManager {

  private val reprioritizedAttachments: HashSet<AttachmentId> = HashSet()

  /**
   * Raise priority of all attachments for the included message records.
   *
   * This is so we can make certain attachments get downloaded more quickly
   */
  fun prioritizeAttachmentsIfNeeded(messageRecords: List<MessageRecord>) {
    SignalExecutors.BOUNDED.execute {
      synchronized(this) {
        val restoringAttachments = messageRecords
          .asSequence()
          .mapNotNull { (it as? MmsMessageRecord?)?.slideDeck?.slides }
          .flatten()
          .mapNotNull { it.asAttachment() as? DatabaseAttachment }
          .filter {
            val needThumbnail = it.thumbnailRestoreState == AttachmentTable.ThumbnailRestoreState.NEEDS_RESTORE && it.transferState == AttachmentTable.TRANSFER_RESTORE_IN_PROGRESS
            (needThumbnail || it.thumbnailRestoreState == AttachmentTable.ThumbnailRestoreState.IN_PROGRESS) && !reprioritizedAttachments.contains(it.attachmentId)
          }
          .map { it.attachmentId to it.mmsId }
          .toSet()

        reprioritizedAttachments += restoringAttachments.map { it.first }

        val thumbnailJobs = restoringAttachments.map { (attachmentId, mmsId) ->
          RestoreAttachmentThumbnailJob(attachmentId = attachmentId, messageId = mmsId, highPriority = true)
        }

        if (thumbnailJobs.isNotEmpty()) {
          AppDependencies.jobManager.addAll(thumbnailJobs)
        }
      }
    }
  }
}
