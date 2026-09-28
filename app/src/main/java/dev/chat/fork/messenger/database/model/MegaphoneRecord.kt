package dev.chat.fork.messenger.database.model

import dev.chat.fork.messenger.megaphone.Megaphones

data class MegaphoneRecord(
  val event: Megaphones.Event,
  val interactionCount: Int,
  val lastInteractionTime: Long,
  val firstVisible: Long,
  val lastVisible: Long,
  val finished: Boolean
)
