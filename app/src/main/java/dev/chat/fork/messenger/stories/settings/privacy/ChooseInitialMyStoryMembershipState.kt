package dev.chat.fork.messenger.stories.settings.privacy

import dev.chat.fork.messenger.recipients.RecipientId
import dev.chat.fork.messenger.stories.settings.my.MyStoryPrivacyState

data class ChooseInitialMyStoryMembershipState(
  val recipientId: RecipientId? = null,
  val privacyState: MyStoryPrivacyState = MyStoryPrivacyState(),
  val allSignalConnectionsCount: Int = 0,
  val hasUserPerformedManualSelection: Boolean = false
)
