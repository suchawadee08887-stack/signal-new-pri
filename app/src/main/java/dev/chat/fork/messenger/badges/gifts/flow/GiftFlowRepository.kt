package dev.chat.fork.messenger.badges.gifts.flow

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.signal.core.util.money.FiatMoney
import org.signal.donations.InAppPaymentType
import dev.chat.fork.messenger.badges.Badges
import dev.chat.fork.messenger.badges.models.Badge
import dev.chat.fork.messenger.components.settings.app.subscription.DonationSerializationHelper.toFiatValue
import dev.chat.fork.messenger.components.settings.app.subscription.InAppPaymentsRepository
import dev.chat.fork.messenger.components.settings.app.subscription.getGiftBadgeAmounts
import dev.chat.fork.messenger.components.settings.app.subscription.getGiftBadges
import dev.chat.fork.messenger.database.InAppPaymentTable
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.database.model.databaseprotos.InAppPaymentData
import dev.chat.fork.messenger.dependencies.AppDependencies
import org.whispersystems.signalservice.internal.push.SubscriptionsConfiguration
import java.util.Currency
import java.util.Locale

/**
 * Repository for grabbing gift badges and supported currency information.
 */
class GiftFlowRepository {

  fun insertInAppPayment(giftSnapshot: GiftFlowState): Single<InAppPaymentTable.InAppPayment> {
    return Single.fromCallable {
      SignalDatabase.inAppPayments.insert(
        type = InAppPaymentType.ONE_TIME_GIFT,
        state = InAppPaymentTable.State.CREATED,
        subscriberId = null,
        endOfPeriod = null,
        inAppPaymentData = InAppPaymentData(
          badge = Badges.toDatabaseBadge(giftSnapshot.giftBadge!!),
          amount = giftSnapshot.giftPrices[giftSnapshot.currency]!!.toFiatValue(),
          level = giftSnapshot.giftLevel!!,
          recipientId = giftSnapshot.recipient!!.id.serialize(),
          additionalMessage = giftSnapshot.additionalMessage?.toString()
        )
      )
    }.flatMap { InAppPaymentsRepository.requireInAppPayment(it) }.subscribeOn(Schedulers.io())
  }

  fun getGiftBadge(): Single<Pair<Int, Badge>> {
    return Single
      .fromCallable {
        AppDependencies.donationsService
          .getDonationsConfiguration(Locale.getDefault())
      }
      .flatMap { it.flattenResult() }
      .map { SubscriptionsConfiguration.GIFT_LEVEL to it.getGiftBadges().first() }
      .subscribeOn(Schedulers.io())
  }

  fun getGiftPricing(): Single<Map<Currency, FiatMoney>> {
    return Single
      .fromCallable {
        AppDependencies.donationsService
          .getDonationsConfiguration(Locale.getDefault())
      }
      .subscribeOn(Schedulers.io())
      .flatMap { it.flattenResult() }
      .map { it.getGiftBadgeAmounts() }
  }
}
