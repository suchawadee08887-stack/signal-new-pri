package dev.chat.fork.messenger.database.model

/**
 * Data needed to know how a distribution privacy settings are configured.
 */
data class DistributionListPrivacyData(
  val privacyMode: DistributionListPrivacyMode,
  val memberCount: Int
)
