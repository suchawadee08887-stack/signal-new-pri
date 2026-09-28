package dev.chat.fork.messenger.preferences

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.signal.core.util.logging.Log
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.util.BackupUtil

class BackupsPreferenceViewModel : ViewModel() {

  private val internalBackupsEnabled = MutableLiveData<Boolean>()
  val backupsEnabled: LiveData<Boolean> = internalBackupsEnabled

  fun refreshBackupStatus() {
    viewModelScope.launch {
      val enabled = withContext(Dispatchers.IO) {
        val context = AppDependencies.application

        if (SignalStore.settings.isBackupEnabled) {
          if (BackupUtil.canUserAccessBackupDirectory(context)) {
            true
          } else {
            Log.w(TAG, "Cannot access backup directory. Disabling backups.")
            BackupUtil.disableBackups(context)
            false
          }
        } else {
          false
        }
      }

      internalBackupsEnabled.value = enabled
    }
  }

  companion object {
    private val TAG = Log.tag(BackupsPreferenceViewModel::class.java)
  }
}
