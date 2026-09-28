/*
 * Copyright 2026 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

@file:JvmName("SendJobUtil")

package dev.chat.fork.messenger.jobs

import org.signal.core.util.logging.Log
import org.signal.network.exceptions.NonSuccessfulResponseCodeException
import dev.chat.fork.messenger.jobmanager.Job
import dev.chat.fork.messenger.jobmanager.JobLogger
import dev.chat.fork.messenger.jobmanager.impl.BackoffUtil
import dev.chat.fork.messenger.transport.RetryLaterException
import dev.chat.fork.messenger.util.RemoteConfig.serverErrorMaxBackoff
import org.whispersystems.signalservice.api.push.exceptions.ProofRequiredException
import org.whispersystems.signalservice.api.push.exceptions.RateLimitException
import org.whispersystems.signalservice.api.push.exceptions.RetryNetworkException
import java.util.concurrent.TimeUnit
import org.signal.libsignal.net.RetryLaterException as LibSignalRetryLaterException

fun Job.getBackoffMillisFromException(tag: String, pastAttemptCount: Int, exception: Exception, default: () -> Long): Long {
  when (exception) {
    is ProofRequiredException -> {
      val backoff = exception.retryAfterSeconds
      Log.w(tag, JobLogger.format(this, "[Proof Required] Retry-After is $backoff seconds."))
      if (backoff >= 0) {
        return TimeUnit.SECONDS.toMillis(backoff)
      }
    }

    is RateLimitException -> {
      val backoff = exception.retryAfterMilliseconds.orElse(-1L)
      if (backoff >= 0) {
        return backoff
      }
    }

    is NonSuccessfulResponseCodeException -> {
      if (exception.is5xx()) {
        return BackoffUtil.exponentialBackoff(pastAttemptCount, serverErrorMaxBackoff)
      }
    }

    is LibSignalRetryLaterException -> {
      return exception.duration.toMillis()
    }

    is RetryNetworkException -> {
      return exception.retryAfterMs
    }

    is RetryLaterException -> {
      val backoff = exception.backoff
      if (backoff >= 0) {
        return backoff
      }
    }
  }

  return default()
}
