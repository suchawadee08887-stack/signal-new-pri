/*
 * Copyright 2026 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.components.settings.app.subscription.donate.gateway

enum class GatewaySelectorBottomSheetEvent {
  GOOGLE_PAY_SELECTED,
  PAYPAL_SELECTED,
  SEPA_SELECTED,
  IDEAL_SELECTED,
  CREDIT_CARD_SELECTED
}
