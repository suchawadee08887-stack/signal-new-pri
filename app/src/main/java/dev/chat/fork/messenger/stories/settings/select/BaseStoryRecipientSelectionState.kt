package dev.chat.fork.messenger.stories.settings.select

import dev.chat.fork.messenger.database.model.DistributionListId
import dev.chat.fork.messenger.database.model.DistributionListRecord
import dev.chat.fork.messenger.recipients.RecipientId

data class BaseStoryRecipientSelectionState(
  val distributionListId: DistributionListId?,
  val privateStory: DistributionListRecord? = null,
  val selection: Set<RecipientId> = emptySet(),
  val isStartingSelection: Boolean = false
)
