/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.calls.links.create

import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.service.webrtc.links.CreateCallLinkResult

sealed interface EnsureCallLinkCreatedResult {
  data class Success(val recipient: Recipient) : EnsureCallLinkCreatedResult
  data class Failure(val failure: CreateCallLinkResult.Failure) : EnsureCallLinkCreatedResult
}
