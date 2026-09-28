package dev.chat.fork.messenger.stories.settings.create

import androidx.navigation.fragment.findNavController
import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.database.model.DistributionListId
import dev.chat.fork.messenger.recipients.RecipientId
import dev.chat.fork.messenger.stories.settings.select.BaseStoryRecipientSelectionFragment
import dev.chat.fork.messenger.util.navigation.safeNavigate

/**
 * Allows user to select who will see the story they are creating
 */
class CreateStoryViewerSelectionFragment : BaseStoryRecipientSelectionFragment() {
  override val actionButtonLabel: Int = R.string.CreateStoryViewerSelectionFragment__next
  override val distributionListId: DistributionListId? = null

  override fun goToNextScreen(recipients: Set<RecipientId>) {
    findNavController().safeNavigate(CreateStoryViewerSelectionFragmentDirections.actionCreateStoryViewerSelectionToCreateStoryWithViewers(recipients.toTypedArray()))
  }
}
