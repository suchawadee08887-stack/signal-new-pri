/*
 * Copyright 2026 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.conversation.drafts

import org.signal.core.util.logging.Log
import dev.chat.fork.messenger.components.voice.VoiceNoteDraft
import dev.chat.fork.messenger.database.SignalDatabase.Companion.drafts
import dev.chat.fork.messenger.dependencies.AppDependencies
import java.io.File

object DraftBlobs {

  private val TAG = Log.tag(DraftBlobs::class)

  fun deleteOrphanedDraftFiles(directory: File) {
    val files = directory.listFiles()

    if (files == null || files.size == 0) {
      Log.d(TAG, "No attachment drafts exist. Skipping.")
      return
    }

    val draftDatabase = drafts
    val voiceNoteDrafts = draftDatabase.getAllVoiceNoteDrafts()

    val draftFileNames = voiceNoteDrafts
      .asSequence()
      .map { VoiceNoteDraft.fromDraft(it) }
      .map(VoiceNoteDraft::uri)
      .mapNotNull { AppDependencies.blobs.buildFileName(it) }
      .toList()

    for (file in files) {
      if (!draftFileNames.contains(file.getName())) {
        if (file.delete()) {
          Log.d(TAG, "Deleted orphaned attachment draft: " + file.getName())
        } else {
          Log.d(TAG, "Failed to delete orphaned attachment draft: " + file.getName())
        }
      }
    }
  }
}
