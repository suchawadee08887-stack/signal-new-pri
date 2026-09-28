package dev.chat.fork.messenger.stories.settings.custom.viewers

import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.database.model.DistributionListId
import dev.chat.fork.messenger.stories.settings.select.BaseStoryRecipientSelectionFragment

/**
 * Allows user to manage users that can view a story for a given distribution list.
 */
class AddViewersFragment : BaseStoryRecipientSelectionFragment() {
  override val actionButtonLabel: Int = R.string.HideStoryFromFragment__done
  override val distributionListId: DistributionListId
    get() = AddViewersFragmentArgs.fromBundle(requireArguments()).distributionListId
}
