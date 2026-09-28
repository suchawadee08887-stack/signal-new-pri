package dev.chat.fork.messenger.components.settings.app.subscription.receipts.detail

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.database.model.InAppPaymentReceiptRecord

class DonationReceiptDetailRepository {
  fun getDonationReceiptRecord(id: Long): Single<InAppPaymentReceiptRecord> {
    return Single.fromCallable<InAppPaymentReceiptRecord> {
      SignalDatabase.donationReceipts.getReceipt(id)!!
    }.subscribeOn(Schedulers.io())
  }
}
