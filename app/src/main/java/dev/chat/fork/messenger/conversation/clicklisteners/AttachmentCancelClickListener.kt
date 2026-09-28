/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */
package dev.chat.fork.messenger.conversation.clicklisteners

import android.view.View
import kotlinx.collections.immutable.toPersistentList
import org.signal.core.util.concurrent.SignalExecutors
import org.signal.core.util.logging.Log
import dev.chat.fork.messenger.attachments.DatabaseAttachment
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.jobs.AttachmentCompressionJob
import dev.chat.fork.messenger.jobs.AttachmentDownloadJob
import dev.chat.fork.messenger.jobs.AttachmentUploadJob
import dev.chat.fork.messenger.mms.Slide
import dev.chat.fork.messenger.mms.SlidesClickedListener

/**
 * Cancels all attachments passed through to the callback.
 *
 * Creates a persistent copy of the handed list of slides to prevent off-thread
 * manipulation.
 */
internal class AttachmentCancelClickListener : SlidesClickedListener {
  override fun onClick(unused: View, slides: List<Slide>) {
    val toCancel = slides.toPersistentList()

    Log.i(TAG, "Canceling compression/upload/download jobs for ${toCancel.size} items")

    SignalExecutors.BOUNDED_IO.execute {
      var cancelCount = 0
      for (slide in toCancel) {
        val attachmentId = (slide.asAttachment() as DatabaseAttachment).attachmentId
        val jobsToCancel = AppDependencies.jobManager.find {
          when (it.factoryKey) {
            AttachmentDownloadJob.KEY -> AttachmentDownloadJob.jobSpecMatchesAttachmentId(it, attachmentId)
            AttachmentCompressionJob.KEY -> AttachmentCompressionJob.jobSpecMatchesAttachmentId(it, attachmentId)
            AttachmentUploadJob.KEY -> AttachmentUploadJob.jobSpecMatchesAttachmentId(it, attachmentId)
            else -> false
          }
        }
        jobsToCancel.forEach {
          AppDependencies.jobManager.cancel(it.id)
          cancelCount++
        }
      }
      Log.i(TAG, "Canceled $cancelCount jobs.")
    }
  }

  companion object {
    private val TAG = Log.tag(AttachmentCancelClickListener::class.java)
  }
}
