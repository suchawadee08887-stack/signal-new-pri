package dev.chat.fork.messenger.components.settings.app.subscription.receipts.list

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.database.model.InAppPaymentReceiptRecord

class DonationReceiptListPageRepository {
  fun getRecords(type: InAppPaymentReceiptRecord.Type?): Single<List<InAppPaymentReceiptRecord>> {
    return Single.fromCallable {
      SignalDatabase.donationReceipts.getReceipts(type)
    }.subscribeOn(Schedulers.io())
  }
}
