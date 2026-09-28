/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.backup.v2.importer

import org.signal.archive.proto.CallLink
import org.signal.core.util.isEmpty
import org.signal.core.util.logging.Log
import org.signal.ringrtc.CallLinkRootKey
import org.signal.ringrtc.CallLinkState
import dev.chat.fork.messenger.backup.v2.ArchiveCallLink
import dev.chat.fork.messenger.database.CallLinkTable
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.recipients.RecipientId
import dev.chat.fork.messenger.service.webrtc.links.CallLinkCredentials
import dev.chat.fork.messenger.service.webrtc.links.CallLinkRoomId
import dev.chat.fork.messenger.service.webrtc.links.SignalCallLinkState
import java.time.Instant

/**
 * Handles the importing of [ArchiveCallLink] models into the local database.
 */
object CallLinkArchiveImporter {

  private val TAG = Log.tag(CallLinkArchiveImporter::class)

  fun import(callLink: ArchiveCallLink): RecipientId? {
    val rootKey: CallLinkRootKey = try {
      CallLinkRootKey(callLink.rootKey.toByteArray())
    } catch (e: Exception) {
      if (callLink.rootKey.isEmpty()) {
        Log.w(TAG, "Missing root key!")
      } else {
        Log.w(TAG, "Failed to parse a non-empty root key!")
      }
      return null
    }

    return SignalDatabase.callLinks.insertCallLink(
      CallLinkTable.CallLink(
        recipientId = RecipientId.UNKNOWN,
        roomId = CallLinkRoomId.fromCallLinkRootKey(rootKey),
        credentials = CallLinkCredentials(callLink.rootKey.toByteArray(), callLink.adminKey?.toByteArray()),
        state = SignalCallLinkState(
          name = callLink.name,
          restrictions = callLink.restrictions.toLocal(),
          expiration = Instant.ofEpochMilli(callLink.expirationMs)
        ),
        deletionTimestamp = 0L
      )
    )
  }
}

private fun CallLink.Restrictions.toLocal(): CallLinkState.Restrictions {
  return when (this) {
    CallLink.Restrictions.ADMIN_APPROVAL -> CallLinkState.Restrictions.ADMIN_APPROVAL
    CallLink.Restrictions.NONE -> CallLinkState.Restrictions.NONE
    CallLink.Restrictions.UNKNOWN -> CallLinkState.Restrictions.UNKNOWN
  }
}
