package dev.chat.fork.messenger

import android.content.Context
import dev.chat.fork.messenger.ContactSelectionListModels.FindByPhoneNumberModel
import dev.chat.fork.messenger.ContactSelectionListModels.FindByUsernameModel
import dev.chat.fork.messenger.ContactSelectionListModels.FindContactsBannerModel
import dev.chat.fork.messenger.ContactSelectionListModels.FindContactsModel
import dev.chat.fork.messenger.ContactSelectionListModels.InviteToSignalModel
import dev.chat.fork.messenger.ContactSelectionListModels.MoreHeaderModel
import dev.chat.fork.messenger.ContactSelectionListModels.NewGroupModel
import dev.chat.fork.messenger.ContactSelectionListModels.RefreshContactsModel
import dev.chat.fork.messenger.contacts.paged.ContactSearchAdapter
import dev.chat.fork.messenger.contacts.paged.ContactSearchConfiguration
import dev.chat.fork.messenger.contacts.paged.ContactSearchData
import dev.chat.fork.messenger.contacts.paged.ContactSearchKey
import dev.chat.fork.messenger.util.adapter.mapping.MappingModel

class ContactSelectionListAdapter(
  context: Context,
  fixedContacts: Set<ContactSearchKey>,
  displayOptions: DisplayOptions,
  onClickCallbacks: OnContactSelectionClick,
  longClickCallbacks: LongClickCallbacks,
  storyContextMenuCallbacks: StoryContextMenuCallbacks,
  callButtonClickCallbacks: CallButtonClickCallbacks
) : ContactSearchAdapter(context, fixedContacts, displayOptions, onClickCallbacks, longClickCallbacks, storyContextMenuCallbacks, callButtonClickCallbacks) {

  init {
    ContactSelectionListModels.registerNewGroup(this, onClickCallbacks::onNewGroupClicked)
    ContactSelectionListModels.registerInviteToSignal(this, onClickCallbacks::onInviteToSignalClicked)
    ContactSelectionListModels.registerFindContacts(this, onClickCallbacks::onFindContactsClicked)
    ContactSelectionListModels.registerFindContactsBanner(this, onClickCallbacks::onDismissFindContactsBannerClicked, onClickCallbacks::onFindContactsClicked)
    ContactSelectionListModels.registerRefreshContacts(this, onClickCallbacks::onRefreshContactsClicked)
    ContactSelectionListModels.registerMoreHeader(this)
    ContactSelectionListModels.registerEmpty(this)
    ContactSelectionListModels.registerFindByUsername(this, onClickCallbacks::onFindByUsernameClicked)
    ContactSelectionListModels.registerFindByPhoneNumber(this, onClickCallbacks::onFindByPhoneNumberClicked)
  }

  class ArbitraryRepository : dev.chat.fork.messenger.contacts.paged.ArbitraryRepository {

    override fun getSize(section: ContactSearchConfiguration.Section.Arbitrary, query: String?): Int {
      return section.types.size
    }

    override fun getData(section: ContactSearchConfiguration.Section.Arbitrary, query: String?, startIndex: Int, endIndex: Int, totalSearchSize: Int): List<ContactSearchData.Arbitrary> {
      check(section.types.size == 1)
      return listOf(ContactSearchData.Arbitrary(section.types.first()))
    }

    override fun getMappingModel(arbitrary: ContactSearchData.Arbitrary): MappingModel<*> {
      return when (ContactSelectionListModels.ArbitraryRow.fromCode(arbitrary.type)) {
        ContactSelectionListModels.ArbitraryRow.NEW_GROUP -> NewGroupModel()
        ContactSelectionListModels.ArbitraryRow.INVITE_TO_SIGNAL -> InviteToSignalModel()
        ContactSelectionListModels.ArbitraryRow.MORE_HEADING -> MoreHeaderModel()
        ContactSelectionListModels.ArbitraryRow.REFRESH_CONTACTS -> RefreshContactsModel()
        ContactSelectionListModels.ArbitraryRow.FIND_CONTACTS -> FindContactsModel()
        ContactSelectionListModels.ArbitraryRow.FIND_CONTACTS_BANNER -> FindContactsBannerModel()
        ContactSelectionListModels.ArbitraryRow.FIND_BY_PHONE_NUMBER -> FindByPhoneNumberModel()
        ContactSelectionListModels.ArbitraryRow.FIND_BY_USERNAME -> FindByUsernameModel()
      }
    }
  }

  interface OnContactSelectionClick : ClickCallbacks {
    fun onNewGroupClicked()
    fun onInviteToSignalClicked()
    fun onRefreshContactsClicked()
    fun onFindContactsClicked()
    fun onDismissFindContactsBannerClicked()
    fun onFindByPhoneNumberClicked()
    fun onFindByUsernameClicked()
  }
}
