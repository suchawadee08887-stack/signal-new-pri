package dev.chat.fork.messenger.components.settings.app.subscription.manage

import dev.chat.fork.messenger.badges.models.Badge
import dev.chat.fork.messenger.database.InAppPaymentTable
import dev.chat.fork.messenger.database.model.databaseprotos.PendingOneTimeDonation
import dev.chat.fork.messenger.subscription.Subscription

data class ManageDonationsState(
  val hasOneTimeBadge: Boolean = false,
  val hasReceipts: Boolean = false,
  val featuredBadge: Badge? = null,
  val isLoaded: Boolean = false,
  val networkError: Boolean = false,
  val availableSubscriptions: List<Subscription> = emptyList(),
  val activeSubscription: InAppPaymentTable.InAppPayment? = null,
  val subscriptionRedemptionState: RedemptionState = RedemptionState.NONE,
  val pendingOneTimeDonation: PendingOneTimeDonation? = null,
  val nonVerifiedMonthlyDonation: NonVerifiedMonthlyDonation? = null,
  val subscriberRequiresCancel: Boolean = false
) {

  enum class RedemptionState {
    NONE,
    IN_PROGRESS,
    SUBSCRIPTION_REFRESH,
    IS_PENDING_BANK_TRANSFER,
    FAILED
  }
}
