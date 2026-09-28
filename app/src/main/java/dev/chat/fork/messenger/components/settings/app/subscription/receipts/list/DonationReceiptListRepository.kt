package dev.chat.fork.messenger.components.settings.app.subscription.receipts.list

import io.reactivex.rxjava3.core.Single
import dev.chat.fork.messenger.badges.Badges
import dev.chat.fork.messenger.components.settings.app.subscription.getBoostBadges
import dev.chat.fork.messenger.components.settings.app.subscription.getGiftBadges
import dev.chat.fork.messenger.components.settings.app.subscription.getSubscriptionLevels
import dev.chat.fork.messenger.database.model.InAppPaymentReceiptRecord
import dev.chat.fork.messenger.dependencies.AppDependencies
import java.util.Locale

class DonationReceiptListRepository {
  fun getBadges(): Single<List<DonationReceiptBadge>> {
    return Single.fromCallable {
      AppDependencies.donationsService
        .getDonationsConfiguration(Locale.getDefault())
    }.map { response ->
      if (response.result.isPresent) {
        val config = response.result.get()
        val boostBadge = DonationReceiptBadge(InAppPaymentReceiptRecord.Type.ONE_TIME_DONATION, -1, config.getBoostBadges().first())
        val giftBadge = DonationReceiptBadge(InAppPaymentReceiptRecord.Type.ONE_TIME_GIFT, -1, config.getGiftBadges().first())
        val subBadges = config.getSubscriptionLevels().map {
          DonationReceiptBadge(
            level = it.key,
            badge = Badges.fromServiceBadge(it.value.badge),
            type = InAppPaymentReceiptRecord.Type.RECURRING_DONATION
          )
        }
        subBadges + boostBadge + giftBadge
      } else {
        emptyList()
      }
    }
  }
}
