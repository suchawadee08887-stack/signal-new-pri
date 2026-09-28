package dev.chat.fork.messenger.components.webrtc

/**
 * This is an interface for [WebRtcAudioPicker31] and [WebRtcAudioPickerLegacy] as a callback for [dev.chat.fork.messenger.components.webrtc.v2.CallAudioToggleButton]
 */
interface AudioStateUpdater {
  fun updateAudioOutputState(audioOutput: WebRtcAudioOutput)
  fun hidePicker()
}
