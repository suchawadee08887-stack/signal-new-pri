package dev.chat.fork.messenger.safety

import dev.chat.fork.messenger.database.model.DistributionListId
import dev.chat.fork.messenger.recipients.Recipient

sealed class SafetyNumberBucket {
  data class DistributionListBucket(val distributionListId: DistributionListId, val name: String) : SafetyNumberBucket()
  data class GroupBucket(val recipient: Recipient) : SafetyNumberBucket()
  object ContactsBucket : SafetyNumberBucket()
}
