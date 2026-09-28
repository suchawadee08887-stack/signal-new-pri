package dev.chat.fork.messenger.contacts.paged

import dev.chat.fork.messenger.conversationlist.chatfilter.ConversationFilterRequest
import dev.chat.fork.messenger.search.SearchFilter

/**
 * Simple search state for contacts.
 */
data class ContactSearchState(
  val query: String? = null,
  val conversationFilterRequest: ConversationFilterRequest? = null,
  val expandedSections: Set<ContactSearchConfiguration.SectionKey> = emptySet(),
  val groupStories: Set<ContactSearchData.Story> = emptySet(),
  val searchFilter: SearchFilter = SearchFilter.EMPTY
)
