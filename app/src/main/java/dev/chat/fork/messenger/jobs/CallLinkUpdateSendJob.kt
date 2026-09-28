/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.jobs

import okio.ByteString.Companion.toByteString
import org.signal.core.util.logging.Log
import org.signal.network.exceptions.PushNetworkException
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.jobmanager.Job
import dev.chat.fork.messenger.jobmanager.impl.NetworkConstraint
import dev.chat.fork.messenger.jobmanager.impl.SealedSenderConstraint
import dev.chat.fork.messenger.jobs.protos.CallLinkUpdateSendJobData
import dev.chat.fork.messenger.service.webrtc.links.CallLinkRoomId
import org.whispersystems.signalservice.api.messages.multidevice.SignalServiceSyncMessage
import org.whispersystems.signalservice.api.push.exceptions.ServerRejectedException
import org.whispersystems.signalservice.internal.push.SyncMessage.CallLinkUpdate
import java.util.concurrent.TimeUnit

/**
 * Sends a [CallLinkUpdate] message to linked devices.
 */
class CallLinkUpdateSendJob private constructor(
  parameters: Parameters,
  private val callLinkRoomId: CallLinkRoomId,
  private val callLinkUpdateType: CallLinkUpdate.Type
) : BaseJob(parameters) {

  companion object {
    const val KEY = "CallLinkUpdateSendJob"
    private val TAG = Log.tag(CallLinkUpdateSendJob::class.java)
  }

  constructor(
    callLinkRoomId: CallLinkRoomId,
    callLinkUpdateType: CallLinkUpdate.Type = CallLinkUpdate.Type.UPDATE
  ) : this(
    Parameters.Builder()
      .setQueue("CallLinkUpdateSendJob")
      .setLifespan(TimeUnit.DAYS.toMillis(1))
      .setMaxAttempts(Parameters.UNLIMITED)
      .addConstraint(NetworkConstraint.KEY)
      .addConstraint(SealedSenderConstraint.KEY)
      .build(),
    callLinkRoomId,
    callLinkUpdateType
  )

  override fun serialize(): ByteArray = CallLinkUpdateSendJobData.Builder()
    .callLinkRoomId(callLinkRoomId.serialize())
    .type(
      when (callLinkUpdateType) {
        CallLinkUpdate.Type.UPDATE -> CallLinkUpdateSendJobData.Type.UPDATE
      }
    )
    .build()
    .encode()

  override fun getFactoryKey(): String = KEY

  override fun onFailure() = Unit

  override fun onRun() {
    val callLink = SignalDatabase.callLinks.getCallLinkByRoomId(callLinkRoomId)
    if (callLink?.credentials == null) {
      Log.i(TAG, "Call link not found or missing credentials. Exiting.")
      return
    }

    val callLinkUpdate = CallLinkUpdate(
      rootKey = callLink.credentials.linkKeyBytes.toByteString(),
      adminPasskey = callLink.credentials.adminPassBytes?.toByteString(),
      type = callLinkUpdateType
    )

    AppDependencies.signalServiceMessageSender
      .sendSyncMessage(SignalServiceSyncMessage.forCallLinkUpdate(callLinkUpdate))
  }

  override fun onShouldRetry(e: Exception): Boolean {
    return when (e) {
      is ServerRejectedException -> false
      is PushNetworkException -> true
      else -> false
    }
  }

  class Factory : Job.Factory<CallLinkUpdateSendJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): CallLinkUpdateSendJob {
      val jobData = CallLinkUpdateSendJobData.ADAPTER.decode(serializedData!!)
      val type: CallLinkUpdate.Type = when (jobData.type) {
        CallLinkUpdateSendJobData.Type.UPDATE, null -> CallLinkUpdate.Type.UPDATE
      }

      return CallLinkUpdateSendJob(
        parameters,
        CallLinkRoomId.DatabaseSerializer.deserialize(jobData.callLinkRoomId),
        type
      )
    }
  }
}
