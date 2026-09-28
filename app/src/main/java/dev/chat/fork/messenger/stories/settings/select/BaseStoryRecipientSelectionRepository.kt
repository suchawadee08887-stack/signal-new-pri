package dev.chat.fork.messenger.stories.settings.select

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.signal.core.util.CursorUtil
import org.signal.core.util.concurrent.SignalExecutors
import dev.chat.fork.messenger.database.RecipientTable
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.database.model.DistributionListId
import dev.chat.fork.messenger.database.model.DistributionListRecord
import dev.chat.fork.messenger.recipients.RecipientId
import dev.chat.fork.messenger.stories.Stories

class BaseStoryRecipientSelectionRepository {

  fun getRecord(distributionListId: DistributionListId): Single<DistributionListRecord> {
    return Single.fromCallable {
      SignalDatabase.distributionLists.getList(distributionListId) ?: error("Record does not exist.")
    }.subscribeOn(Schedulers.io())
  }

  fun updateDistributionListMembership(distributionListRecord: DistributionListRecord, recipients: Set<RecipientId>) {
    SignalExecutors.BOUNDED.execute {
      val currentRecipients = SignalDatabase.distributionLists.getRawMembers(distributionListRecord.id, distributionListRecord.privacyMode).toSet()
      val oldNotNew = currentRecipients - recipients
      val newNotOld = recipients - currentRecipients

      oldNotNew.forEach {
        SignalDatabase.distributionLists.removeMemberFromList(distributionListRecord.id, distributionListRecord.privacyMode, it)
      }

      newNotOld.forEach {
        SignalDatabase.distributionLists.addMemberToList(distributionListRecord.id, distributionListRecord.privacyMode, it)
      }

      Stories.onStorySettingsChanged(distributionListRecord.id)
    }
  }

  fun getAllSignalContacts(): Single<Set<RecipientId>> {
    return Single.fromCallable {
      SignalDatabase.recipients.getSignalContacts(RecipientTable.IncludeSelfMode.Exclude).use {
        val recipientSet = mutableSetOf<RecipientId>()
        while (it.moveToNext()) {
          recipientSet.add(RecipientId.from(CursorUtil.requireLong(it, RecipientTable.ID)))
        }

        recipientSet
      } ?: emptySet()
    }.subscribeOn(Schedulers.io())
  }
}
