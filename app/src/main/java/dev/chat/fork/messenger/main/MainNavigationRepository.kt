package dev.chat.fork.messenger.main

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import dev.chat.fork.messenger.database.RxDatabaseObserver
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.recipients.Recipient

object MainNavigationRepository {

  fun getNumberOfUnreadMessages(): Flow<Long> {
    return RxDatabaseObserver.conversationList.map { SignalDatabase.threads.getUnreadMessageCount() }.asFlow()
  }

  fun getNumberOfUnseenStories(): Flow<Long> {
    return RxDatabaseObserver.conversationList.map {
      SignalDatabase
        .messages
        .getUnreadStoryThreadRecipientIds()
        .map { Recipient.resolved(it) }
        .filterNot { it.shouldHideStory }
        .size
        .toLong()
    }.asFlow()
  }

  fun getHasFailedOutgoingStories(): Flow<Boolean> {
    return RxDatabaseObserver.conversationList.map { SignalDatabase.messages.hasFailedOutgoingStory() }.asFlow()
  }

  fun getNumberOfUnseenCalls(): Flow<Long> {
    return RxDatabaseObserver.conversationList.map { SignalDatabase.calls.getUnreadMissedCallCount() }.asFlow()
  }
}
