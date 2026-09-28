/*
 * Copyright 2026 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.video.interfaces

import android.content.Context
import android.net.Uri
import okio.IOException

interface MediaInputFactory {
  @Throws(IOException::class)
  fun createForUri(context: Context, uri: Uri): MediaInput
}
