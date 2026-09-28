package dev.chat.fork.messenger.conversation.v2.groups

import dev.chat.fork.messenger.database.GroupTable

/**
 * @param groupTableMemberLevel Self membership level
 * @param isAnnouncementGroup   Whether the group is an announcement group.
 */
data class ConversationGroupMemberLevel(
  val groupTableMemberLevel: GroupTable.MemberLevel,
  val isAnnouncementGroup: Boolean,
  val allMembersCanEditGroupInfo: Boolean
)
