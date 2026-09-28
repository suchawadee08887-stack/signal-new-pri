package dev.chat.fork.messenger.stories.archive

import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.jobs.MultiDeviceDeleteSyncJob

class StoryArchiveRepository {

  fun deleteStories(messageIds: Set<Long>) {
    val records = messageIds.mapNotNull { SignalDatabase.messages.getMessageRecordOrNull(it) }.toSet()
    messageIds.forEach { SignalDatabase.messages.deleteMessage(it) }
    MultiDeviceDeleteSyncJob.enqueueMessageDeletes(records)
  }
}
