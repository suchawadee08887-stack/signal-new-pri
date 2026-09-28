package dev.chat.fork.messenger.service.webrtc

import org.signal.core.models.ServiceId.ACI
import org.signal.ringrtc.CallManager
import dev.chat.fork.messenger.groups.GroupId
import dev.chat.fork.messenger.recipients.RecipientId

data class GroupCallRingCheckInfo(
  val recipientId: RecipientId,
  val groupId: GroupId.V2,
  val ringId: Long,
  val ringerAci: ACI,
  val ringUpdate: CallManager.RingUpdate
)
