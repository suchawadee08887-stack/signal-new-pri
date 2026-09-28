package dev.chat.fork.messenger.recipients.ui.bottomsheet

import dev.chat.fork.messenger.groups.memberlabel.StyledMemberLabel

data class RecipientDetailsState(
  val memberLabel: StyledMemberLabel?,
  val aboutText: String?
)
