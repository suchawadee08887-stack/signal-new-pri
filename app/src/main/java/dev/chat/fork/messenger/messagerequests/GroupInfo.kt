package dev.chat.fork.messenger.messagerequests

import dev.chat.fork.messenger.recipients.Recipient

/**
 * Group info needed to show message request state UX.
 */
class GroupInfo(
  val fullMemberCount: Int = 0,
  val pendingMemberCount: Int = 0,
  val description: String = "",
  val hasExistingContacts: Boolean = false,
  val membersPreview: List<Recipient> = emptyList(),
  val isMember: Boolean = false,
  val isTerminated: Boolean = false,
  val nameVerified: Boolean = false
) {
  companion object {
    @JvmField
    val ZERO = GroupInfo()
  }
}
