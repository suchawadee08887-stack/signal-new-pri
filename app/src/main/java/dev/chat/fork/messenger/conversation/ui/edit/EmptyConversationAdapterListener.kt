/*
 * Copyright 2026 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.conversation.ui.edit

import android.net.Uri
import android.view.View
import androidx.lifecycle.Observer
import org.signal.ringrtc.CallLinkRootKey
import dev.chat.fork.messenger.components.voice.VoiceNotePlaybackState
import dev.chat.fork.messenger.contactshare.Contact
import dev.chat.fork.messenger.conversation.ConversationAdapter
import dev.chat.fork.messenger.conversation.ConversationItem
import dev.chat.fork.messenger.conversation.ConversationMessage
import dev.chat.fork.messenger.conversation.mutiselect.MultiselectPart
import dev.chat.fork.messenger.database.model.InMemoryMessageRecord
import dev.chat.fork.messenger.database.model.MessageRecord
import dev.chat.fork.messenger.database.model.MmsMessageRecord
import dev.chat.fork.messenger.groups.GroupId
import dev.chat.fork.messenger.groups.GroupMigrationMembershipChange
import dev.chat.fork.messenger.linkpreview.LinkPreview
import dev.chat.fork.messenger.mediapreview.MediaIntentFactory
import dev.chat.fork.messenger.polls.PollOption
import dev.chat.fork.messenger.polls.PollRecord
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.recipients.RecipientId
import dev.chat.fork.messenger.stickers.StickerLocator

/**
 * Empty object for when a callback can't be found.
 */
object EmptyConversationAdapterListener : ConversationAdapter.ItemClickListener {
  override fun onItemClick(item: MultiselectPart?) = Unit
  override fun onItemLongClick(itemView: View?, item: MultiselectPart?) = Unit
  override fun onQuoteClicked(messageRecord: MmsMessageRecord?) = Unit
  override fun onLinkPreviewClicked(linkPreview: LinkPreview) = Unit
  override fun onQuotedIndicatorClicked(messageRecord: MessageRecord) = Unit
  override fun onMoreTextClicked(conversationRecipientId: RecipientId, messageId: Long, isMms: Boolean) = Unit
  override fun onStickerClicked(stickerLocator: StickerLocator) = Unit
  override fun onViewOnceMessageClicked(messageRecord: MmsMessageRecord) = Unit
  override fun onSharedContactDetailsClicked(contact: Contact, avatarTransitionView: View) = Unit
  override fun onAddToContactsClicked(contact: Contact) = Unit
  override fun onMessageSharedContactClicked(choices: List<Recipient?>) = Unit
  override fun onInviteSharedContactClicked(choices: List<Recipient?>) = Unit
  override fun onReactionClicked(multiselectPart: MultiselectPart, messageId: Long, isMms: Boolean) = Unit
  override fun onGroupMemberClicked(recipientId: RecipientId, groupId: GroupId) = Unit
  override fun onMessageWithErrorClicked(messageRecord: MessageRecord) = Unit
  override fun onMessageWithRecaptchaNeededClicked(messageRecord: MessageRecord) = Unit
  override fun onIncomingIdentityMismatchClicked(recipientId: RecipientId) = Unit
  override fun onRegisterVoiceNoteCallbacks(onPlaybackStartObserver: Observer<VoiceNotePlaybackState?>) = Unit
  override fun onUnregisterVoiceNoteCallbacks(onPlaybackStartObserver: Observer<VoiceNotePlaybackState?>) = Unit
  override fun onVoiceNotePause(uri: Uri) = Unit
  override fun onVoiceNotePlay(uri: Uri, messageId: Long, position: Double) = Unit
  override fun onVoiceNoteSeekTo(uri: Uri, position: Double) = Unit
  override fun onVoiceNotePlaybackSpeedChanged(uri: Uri, speed: Float) = Unit
  override fun onGroupMigrationLearnMoreClicked(membershipChange: GroupMigrationMembershipChange) = Unit
  override fun onChatSessionRefreshLearnMoreClicked() = Unit
  override fun onBadDecryptLearnMoreClicked(author: RecipientId) = Unit
  override fun onSafetyNumberLearnMoreClicked(recipient: Recipient) = Unit
  override fun onJoinGroupCallClicked() = Unit
  override fun onInviteFriendsToGroupClicked(groupId: GroupId.V2) = Unit
  override fun onEnableCallNotificationsClicked() = Unit
  override fun onPlayInlineContent(conversationMessage: ConversationMessage?) = Unit
  override fun onInMemoryMessageClicked(messageRecord: InMemoryMessageRecord) = Unit
  override fun onViewGroupDescriptionChange(groupId: GroupId?, description: String, isMessageRequestAccepted: Boolean) = Unit
  override fun onChangeNumberUpdateContact(recipient: Recipient) = Unit
  override fun onChangeProfileNameUpdateContact(recipient: Recipient) = Unit
  override fun onCallToAction(action: String) = Unit
  override fun onDonateClicked() = Unit
  override fun onBlockJoinRequest(recipient: Recipient) = Unit
  override fun onRecipientNameClicked(target: RecipientId) = Unit
  override fun onInviteToSignalClicked() = Unit
  override fun onActivatePaymentsClicked() = Unit
  override fun onSendPaymentClicked(recipientId: RecipientId) = Unit
  override fun onScheduledIndicatorClicked(view: View, conversationMessage: ConversationMessage) = Unit
  override fun onUrlClicked(url: String): Boolean = false
  override fun onViewGiftBadgeClicked(messageRecord: MessageRecord) = Unit
  override fun onGiftBadgeRevealed(messageRecord: MessageRecord) = Unit
  override fun goToMediaPreview(parent: ConversationItem?, sharedElement: View?, args: MediaIntentFactory.MediaPreviewArgs?) = Unit
  override fun onEditedIndicatorClicked(conversationMessage: ConversationMessage) = Unit
  override fun onShowGroupDescriptionClicked(groupName: String, description: String, shouldLinkifyWebLinks: Boolean) = Unit
  override fun onJoinCallLink(callLinkRootKey: CallLinkRootKey) = Unit
  override fun onShowSafetyTips(forGroup: Boolean) = Unit
  override fun onReportSpamLearnMoreClicked() = Unit
  override fun onMessageRequestAcceptOptionsClicked() = Unit
  override fun onItemDoubleClick(multiselectPart: MultiselectPart?) = Unit
  override fun onPaymentTombstoneClicked() = Unit
  override fun onDisplayMediaNoLongerAvailableSheet() = Unit
  override fun onShowUnverifiedProfileSheet(forGroup: Boolean) = Unit
  override fun onUpdateSignalClicked() = Unit
  override fun onViewResultsClicked(pollId: Long) = Unit
  override fun onViewPollClicked(messageId: Long) = Unit
  override fun onToggleVote(poll: PollRecord, pollOption: PollOption, isChecked: Boolean?) = Unit
  override fun onViewPinnedMessage(messageId: Long) = Unit
  override fun onExpandEvents(messageId: Long, itemView: View, collapsedSize: Int) = Unit
  override fun onCollapseEvents(messageId: Long, itemView: View, collapsedSize: Int) = Unit
}
