package dev.chat.fork.messenger.components.settings.app.internal.donor

import org.signal.donations.StripeDeclineCode
import dev.chat.fork.messenger.badges.models.Badge
import dev.chat.fork.messenger.components.settings.app.subscription.errors.UnexpectedSubscriptionCancellation

data class InternalDonorErrorConfigurationState(
  val badges: List<Badge> = emptyList(),
  val selectedBadge: Badge? = null,
  val selectedUnexpectedSubscriptionCancellation: UnexpectedSubscriptionCancellation? = null,
  val selectedStripeDeclineCode: StripeDeclineCode.Code? = null
)
