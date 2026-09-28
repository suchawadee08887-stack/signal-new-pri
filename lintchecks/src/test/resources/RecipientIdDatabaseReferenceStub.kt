package dev.chat.fork.messenger.database

internal interface RecipientIdDatabaseReference {
  fun remapRecipient(fromId: RecipientId?, toId: RecipientId?)
}
