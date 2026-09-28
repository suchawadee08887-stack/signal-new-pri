package dev.chat.fork.messenger.components.settings.app

import androidx.compose.runtime.Immutable
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.util.Environment
import dev.chat.fork.messenger.util.RemoteConfig

@Immutable
data class AppSettingsState(
  val isPrimaryDevice: Boolean,
  val unreadPaymentsCount: Int,
  val hasExpiredGiftBadge: Boolean,
  val allowUserToGoToDonationManagementScreen: Boolean,
  val userUnregistered: Boolean,
  val clientDeprecated: Boolean,
  val showInternalPreferences: Boolean = RemoteConfig.internalUser,
  val showPayments: Boolean = SignalStore.payments.paymentsAvailability.showPaymentsMenu(),
  val showAppUpdates: Boolean = Environment.IS_NIGHTLY,
  val backupFailureState: BackupFailureState = BackupFailureState.NONE
) {
  fun isRegisteredAndUpToDate(): Boolean {
    return !userUnregistered && !clientDeprecated
  }
}
