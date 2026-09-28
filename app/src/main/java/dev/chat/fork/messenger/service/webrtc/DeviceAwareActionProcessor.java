package dev.chat.fork.messenger.service.webrtc;

import androidx.annotation.NonNull;

import org.signal.core.util.logging.Log;
import dev.chat.fork.messenger.components.webrtc.BroadcastVideoSink;
import dev.chat.fork.messenger.events.WebRtcViewModel;
import dev.chat.fork.messenger.ringrtc.CameraState;
import dev.chat.fork.messenger.ringrtc.RemotePeer;
import dev.chat.fork.messenger.events.CallParticipant;
import dev.chat.fork.messenger.service.webrtc.state.WebRtcServiceState;
import dev.chat.fork.messenger.webrtc.audio.SignalAudioManager;

import java.util.Set;

/**
 * Encapsulates the shared logic to deal with local device actions. Other action processors inherit
 * the behavior by extending it instead of delegating. It is not intended to be the main processor
 * for the system.
 */
public abstract class DeviceAwareActionProcessor extends WebRtcActionProcessor {

  public DeviceAwareActionProcessor(@NonNull WebRtcInteractor webRtcInteractor, @NonNull String tag) {
    super(webRtcInteractor, tag);
  }

  @Override
  protected @NonNull WebRtcServiceState handleAudioDeviceChanged(@NonNull WebRtcServiceState currentState, @NonNull SignalAudioManager.AudioDevice activeDevice, @NonNull Set<SignalAudioManager.AudioDevice> availableDevices) {
    Log.i(tag, "handleAudioDeviceChanged(): active: " + activeDevice + " available: " + availableDevices);

    if (currentState.getCallInfoState().getCallState() == WebRtcViewModel.State.CALL_CONNECTED) {
      boolean localVideoEnabled  = currentState.getLocalDeviceState().getCameraState().isEnabled() || currentState.getLocalDeviceState().isScreenSharing();
      boolean remoteVideoEnabled = currentState.getCallInfoState().getRemoteCallParticipantsMap().values().stream().anyMatch(CallParticipant::isVideoEnabled);
      webRtcInteractor.updatePhoneState(WebRtcUtil.getInCallPhoneState(context, localVideoEnabled, remoteVideoEnabled));
    } else {
      Log.i(tag, "handleAudioDeviceChanged(): call not connected, not updating phone state");
    }

    return currentState.builder()
                       .changeLocalDeviceState()
                       .setActiveDevice(activeDevice)
                       .setAvailableDevices(availableDevices)
                       .setAudioDeviceChangePending(false)
                       .build();
  }

  @Override
  protected @NonNull WebRtcServiceState handleSetUserAudioDevice(@NonNull WebRtcServiceState currentState, @NonNull SignalAudioManager.ChosenAudioDeviceIdentifier userDevice) {
    Log.i(tag, "handleSetUserAudioDevice(): userDevice: " + userDevice);

    RemotePeer activePeer = currentState.getCallInfoState().getActivePeer();
    webRtcInteractor.setUserAudioDevice(activePeer != null ? activePeer.getId() : null, userDevice);

    return currentState.builder()
                       .changeLocalDeviceState()
                       .setAudioDeviceChangePending(true)
                       .build();
  }

  @Override
  protected @NonNull WebRtcServiceState handleSetCameraFlip(@NonNull WebRtcServiceState currentState) {
    Log.i(tag, "handleSetCameraFlip():");

    if (currentState.getLocalDeviceState().getCameraState().isEnabled() && currentState.getVideoState().getRouter() != null) {
      currentState.getVideoState().getRouter().flip();
      return currentState.builder()
                         .changeLocalDeviceState()
                         .cameraState(currentState.getVideoState().getRouter().getCameraState())
                         .build();
    }
    return currentState;
  }

  @Override
  public @NonNull WebRtcServiceState handleCameraSwitchCompleted(@NonNull WebRtcServiceState currentState, @NonNull CameraState newCameraState) {
    Log.i(tag, "handleCameraSwitchCompleted():");

    BroadcastVideoSink localSink = currentState.getVideoState().getLocalSink();
    if (localSink != null) {
      localSink.setRotateToRightSide(newCameraState.getActiveDirection() == CameraState.Direction.BACK);
    }

    return currentState.builder()
                       .changeLocalDeviceState()
                       .cameraState(newCameraState)
                       .build();
  }
}
