package dev.chat.fork.messenger.backup.v2.ui.verify

import android.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.signal.core.ui.compose.ComposeFragment
import dev.chat.fork.messenger.backup.v2.ui.subscription.MessageBackupsKeyRecordMode
import dev.chat.fork.messenger.backup.v2.ui.subscription.MessageBackupsKeyRecordScreen
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.util.viewModel

/**
 * Fragment which displays the backup key to the user after users forget it.
 */
class ForgotBackupKeyFragment : ComposeFragment() {

  companion object {
    const val CLIPBOARD_TIMEOUT_SECONDS = 60
  }

  private val viewModel: ForgotBackupKeyViewModel by viewModel { ForgotBackupKeyViewModel() }

  @Composable
  override fun FragmentContent() {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    MessageBackupsKeyRecordScreen(
      backupKey = SignalStore.account.accountEntropyPool.displayValue,
      keySaveState = state.keySaveState,
      backupKeyCredentialManagerHandler = viewModel,
      mode = remember {
        MessageBackupsKeyRecordMode.Next(onNextClick = {
          requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .add(R.id.content, ConfirmBackupKeyDisplayFragment())
            .addToBackStack(null)
            .commit()
        })
      }
    )
  }
}
