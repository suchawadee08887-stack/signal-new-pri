package dev.chat.fork.messenger.components.settings.app.subscription.receipts.detail

import dev.chat.fork.messenger.database.model.InAppPaymentReceiptRecord

data class DonationReceiptDetailState(
  val inAppPaymentReceiptRecord: InAppPaymentReceiptRecord? = null
)
