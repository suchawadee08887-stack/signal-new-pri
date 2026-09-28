package dev.chat.fork.messenger.mediasend.v2.capture

import org.signal.core.models.media.Media
import dev.chat.fork.messenger.recipients.Recipient

sealed interface MediaCaptureEvent {
  data class MediaCaptureRendered(val media: Media) : MediaCaptureEvent
  data class UsernameScannedFromQrCode(val recipient: Recipient, val username: String) : MediaCaptureEvent
  data object DeviceLinkScannedFromQrCode : MediaCaptureEvent
  data object MediaCaptureRenderFailed : MediaCaptureEvent
  data class ReregistrationScannedFromQrCode(val data: String) : MediaCaptureEvent
}
