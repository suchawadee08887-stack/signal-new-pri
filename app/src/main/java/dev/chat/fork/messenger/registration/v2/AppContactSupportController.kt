/*
 * Copyright 2026 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.registration.v2

import android.content.Context
import kotlinx.coroutines.suspendCancellableCoroutine
import org.signal.core.util.logging.Log
import org.signal.core.util.orNull
import org.signal.registration.ContactSupportController
import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.logsubmit.SubmitDebugLogRepository
import dev.chat.fork.messenger.util.CommunicationActions
import dev.chat.fork.messenger.util.SupportEmailUtil
import kotlin.coroutines.resume

/**
 * App-side implementation of the registration module's [ContactSupportController].
 */
class AppContactSupportController : ContactSupportController {

  companion object {
    val TAG = Log.tag(AppContactSupportController::class.java)
  }

  override suspend fun uploadDebugLog(): String? {
    return suspendCancellableCoroutine { continuation ->
      try {
        SubmitDebugLogRepository().buildAndSubmitLog { result ->
          continuation.resume(result.orNull())
        }
      } catch (e: Throwable) {
        Log.w(TAG, "Failed to submit debug log.", e)
        continuation.resume(null)
      }
    }
  }

  override fun sendSupportEmail(context: Context, subject: String, filter: String, debugLogUrl: String?) {
    val prefix = if (debugLogUrl != null) {
      "\n${context.getString(R.string.HelpFragment__debug_log)} $debugLogUrl\n\n"
    } else {
      ""
    }

    val body = SupportEmailUtil.generateSupportEmailBody(context, filter, prefix, null)
    CommunicationActions.openEmail(context, SupportEmailUtil.getSupportEmailAddress(context), subject, body)
  }
}
