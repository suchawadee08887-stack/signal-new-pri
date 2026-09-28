/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.devicetransfer.newdevice

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import dev.chat.fork.messenger.database.model.databaseprotos.RestoreDecisionState
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.jobs.ReclaimUsernameAndLinkJob
import dev.chat.fork.messenger.keyvalue.Completed
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.registration.data.RegistrationRepository
import dev.chat.fork.messenger.registration.util.RegistrationUtil

class NewDeviceTransferViewModel : ViewModel() {
  fun onRestoreComplete(context: Context, onComplete: () -> Unit) {
    viewModelScope.launch {
      SignalStore.registration.localRegistrationMetadata?.let { metadata ->
        RegistrationRepository.registerAccountLocally(context, metadata)
        SignalStore.registration.localRegistrationMetadata = null
        RegistrationUtil.maybeMarkRegistrationComplete()

        SignalStore.misc.needsUsernameRestore = true
        AppDependencies.jobManager.add(ReclaimUsernameAndLinkJob())
      }

      SignalStore.registration.restoreDecisionState = RestoreDecisionState.Completed

      withContext(Dispatchers.Main) {
        onComplete()
      }
    }
  }
}
