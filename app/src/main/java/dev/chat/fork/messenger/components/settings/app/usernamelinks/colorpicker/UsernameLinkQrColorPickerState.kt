package dev.chat.fork.messenger.components.settings.app.usernamelinks.colorpicker

import kotlinx.collections.immutable.ImmutableList
import dev.chat.fork.messenger.components.settings.app.usernamelinks.QrCodeState
import dev.chat.fork.messenger.components.settings.app.usernamelinks.UsernameQrCodeColorScheme

data class UsernameLinkQrColorPickerState(
  val username: String,
  val qrCodeData: QrCodeState,
  val colorSchemes: ImmutableList<UsernameQrCodeColorScheme>,
  val selectedColorScheme: UsernameQrCodeColorScheme
)
