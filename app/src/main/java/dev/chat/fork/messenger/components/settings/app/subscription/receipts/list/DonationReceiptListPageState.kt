package dev.chat.fork.messenger.components.settings.app.subscription.receipts.list

import dev.chat.fork.messenger.database.model.InAppPaymentReceiptRecord

data class DonationReceiptListPageState(
  val records: List<InAppPaymentReceiptRecord> = emptyList(),
  val isLoaded: Boolean = false
)
