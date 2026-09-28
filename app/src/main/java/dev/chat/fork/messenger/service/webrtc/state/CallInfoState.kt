package dev.chat.fork.messenger.service.webrtc.state

import org.signal.ringrtc.CallId
import org.signal.ringrtc.CallManager.CallEndReason
import org.signal.ringrtc.GroupCall
import dev.chat.fork.messenger.events.CallParticipant
import dev.chat.fork.messenger.events.CallParticipantId
import dev.chat.fork.messenger.events.GroupCallSpeechEvent
import dev.chat.fork.messenger.events.WebRtcViewModel
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.recipients.RecipientId
import dev.chat.fork.messenger.ringrtc.RemotePeer
import dev.chat.fork.messenger.service.webrtc.CallLinkDisconnectReason
import dev.chat.fork.messenger.service.webrtc.PendingParticipantCollection
import java.util.Optional

/**
 * General state of ongoing calls.
 *
 * @param pendingParticipants A list of pending users wishing to join a given call link.
 */
data class CallInfoState(
  var callState: WebRtcViewModel.State = WebRtcViewModel.State.IDLE,
  var callRecipient: Recipient = Recipient.UNKNOWN,
  var callConnectedTime: Long = -1,
  @get:JvmName("getRemoteCallParticipantsMap") var remoteParticipants: MutableMap<CallParticipantId, CallParticipant> = mutableMapOf(),
  var peerMap: MutableMap<Int, RemotePeer> = mutableMapOf(),
  var activePeer: RemotePeer? = null,
  var groupCall: GroupCall? = null,
  @get:JvmName("getGroupCallState") var groupState: WebRtcViewModel.GroupCallState = WebRtcViewModel.GroupCallState.IDLE,
  var identityChangedRecipients: MutableSet<RecipientId> = mutableSetOf(),
  var remoteDevicesCount: Optional<Long> = Optional.empty(),
  var participantLimit: Long? = null,
  var pendingParticipants: PendingParticipantCollection = PendingParticipantCollection(),
  var callLinkDisconnectReason: CallLinkDisconnectReason? = null,
  var groupCallEndReason: CallEndReason? = null,
  var groupCallSpeechEvent: GroupCallSpeechEvent? = null
) {

  val remoteCallParticipants: List<CallParticipant>
    get() = ArrayList(remoteParticipants.values)

  fun getRemoteCallParticipant(recipient: Recipient): CallParticipant? {
    return getRemoteCallParticipant(CallParticipantId(recipient))
  }

  fun getRemoteCallParticipant(callParticipantId: CallParticipantId): CallParticipant? {
    return remoteParticipants[callParticipantId]
  }

  fun getPeer(hashCode: Int): RemotePeer? {
    return peerMap[hashCode]
  }

  fun getPeerByCallId(callId: CallId): RemotePeer? {
    return peerMap.values.firstOrNull { it.callId == callId }
  }

  fun requireActivePeer(): RemotePeer {
    return activePeer!!
  }

  fun requireGroupCall(): GroupCall {
    return groupCall!!
  }

  fun duplicate(): CallInfoState = copy(
    remoteParticipants = remoteParticipants.toMutableMap(),
    peerMap = peerMap.toMutableMap(),
    identityChangedRecipients = identityChangedRecipients.toMutableSet()
  )
}
