package dev.chat.fork.messenger.components.settings.app.data

import org.signal.mediasend.SentMediaQuality
import dev.chat.fork.messenger.webrtc.CallDataMode

data class DataAndStorageSettingsState(
  val totalStorageUse: Long,
  val mobileAutoDownloadValues: Set<String>,
  val wifiAutoDownloadValues: Set<String>,
  val roamingAutoDownloadValues: Set<String>,
  val callDataMode: CallDataMode,
  val isProxyEnabled: Boolean,
  val sentMediaQuality: SentMediaQuality,
  val forceWebsocketMode: Boolean,
  val playServicesAvailable: Boolean,
  val showStayConnectedDialog: Boolean
)
