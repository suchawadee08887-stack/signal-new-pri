/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.backup.v2.database

import org.signal.core.models.database.AttachmentId
import dev.chat.fork.messenger.attachments.Attachment
import dev.chat.fork.messenger.database.AttachmentTable

fun AttachmentTable.restoreWallpaperAttachment(attachment: Attachment): AttachmentId? {
  return insertAttachmentsForMessage(AttachmentTable.WALLPAPER_MESSAGE_ID, listOf(attachment), emptyList()).values.firstOrNull()
}
