package dev.chat.fork.messenger.stories.settings.create

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import org.signal.core.ui.enableEdgeToEdge
import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.recipients.RecipientId
import dev.chat.fork.messenger.stories.settings.select.BaseStoryRecipientSelectionFragment

class CreateStoryFlowDialogFragment : DialogFragment(R.layout.create_story_flow_dialog_fragment), BaseStoryRecipientSelectionFragment.Callback, CreateStoryWithViewersFragment.Callback {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NO_FRAME, R.style.Signal_DayNight_Dialog_FullScreen)
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return super.onCreateDialog(savedInstanceState).apply {
      window?.enableEdgeToEdge()
    }
  }

  override fun exitFlow() {
    dismissAllowingStateLoss()
  }

  override fun onDone(recipientId: RecipientId) {
    setFragmentResult(
      CreateStoryWithViewersFragment.REQUEST_KEY,
      Bundle().apply {
        putParcelable(CreateStoryWithViewersFragment.STORY_RECIPIENT, recipientId)
      }
    )
    dismissAllowingStateLoss()
  }
}
