package dev.chat.fork.messenger.components.settings.app.subscription.receipts.list

import dev.chat.fork.messenger.badges.models.Badge
import dev.chat.fork.messenger.database.model.InAppPaymentReceiptRecord

data class DonationReceiptBadge(
  val type: InAppPaymentReceiptRecord.Type,
  val level: Int,
  val badge: Badge
)
