package dev.chat.fork.messenger.database

internal interface ThreadIdDatabaseReference {
  fun remapThread(fromId: Long, toId: Long)
}
