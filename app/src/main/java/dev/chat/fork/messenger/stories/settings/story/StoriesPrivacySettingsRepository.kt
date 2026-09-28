package dev.chat.fork.messenger.stories.settings.story

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.signal.core.util.concurrent.SignalExecutors
import dev.chat.fork.messenger.database.GroupTable
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.recipients.RecipientId
import dev.chat.fork.messenger.sms.MessageSender
import dev.chat.fork.messenger.storage.StorageSyncHelper
import dev.chat.fork.messenger.stories.Stories

class StoriesPrivacySettingsRepository {
  fun markGroupsAsStories(groups: List<RecipientId>): Completable {
    return Completable.fromCallable {
      SignalDatabase.groups.setShowAsStoryState(groups, GroupTable.ShowAsStoryState.ALWAYS)
      SignalDatabase.recipients.markNeedsSync(groups)
      StorageSyncHelper.scheduleSyncForDataChange()
    }
  }

  fun setStoriesEnabled(isEnabled: Boolean): Completable {
    return Completable.fromAction {
      SignalStore.story.isFeatureDisabled = !isEnabled
      Stories.onStorySettingsChanged(Recipient.self().id)
      AppDependencies.resetNetwork()

      SignalDatabase.messages.getAllOutgoingStories(false, -1).use { reader ->
        reader.map { record -> record.id }
      }.forEach { messageId ->
        MessageSender.sendRemoteDelete(messageId)
      }
    }.subscribeOn(Schedulers.io())
  }

  fun onSettingsChanged() {
    SignalExecutors.BOUNDED_IO.execute {
      Stories.onStorySettingsChanged(Recipient.self().id)
    }
  }

  fun userHasOutgoingStories(): Single<Boolean> {
    return Single.fromCallable {
      SignalDatabase.messages.getAllOutgoingStories(false, -1).use {
        it.iterator().hasNext()
      }
    }.subscribeOn(Schedulers.io())
  }
}
