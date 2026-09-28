package dev.chat.fork.messenger.service.webrtc.state

import android.content.Intent
import dev.chat.fork.messenger.components.sensors.Orientation
import dev.chat.fork.messenger.events.CallParticipant
import dev.chat.fork.messenger.ringrtc.CameraState
import dev.chat.fork.messenger.webrtc.audio.SignalAudioManager
import org.webrtc.PeerConnection

/**
 * Local device specific state.
 */
data class LocalDeviceState(
  var cameraState: CameraState = CameraState.UNKNOWN,
  var isMicrophoneEnabled: Boolean = true,
  var orientation: Orientation = Orientation.PORTRAIT_BOTTOM_EDGE,
  var isLandscapeEnabled: Boolean = false,
  var deviceOrientation: Orientation = Orientation.PORTRAIT_BOTTOM_EDGE,
  var activeDevice: SignalAudioManager.AudioDevice = SignalAudioManager.AudioDevice.NONE,
  var availableDevices: Set<SignalAudioManager.AudioDevice> = emptySet(),
  var bluetoothPermissionDenied: Boolean = false,
  var isAudioDeviceChangePending: Boolean = false,
  var networkConnectionType: PeerConnection.AdapterType = PeerConnection.AdapterType.UNKNOWN,
  var handRaisedTimestamp: Long = CallParticipant.HAND_LOWERED,
  var remoteMutedBy: CallParticipant? = null,
  var isScreenSharing: Boolean = false,
  var mediaProjectionIntent: Intent? = null
) {

  fun duplicate(): LocalDeviceState {
    return copy()
  }
}
