package dev.chat.fork.messenger.safety

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import dev.chat.fork.messenger.contacts.paged.ContactSearchKey
import dev.chat.fork.messenger.database.model.MessageId
import dev.chat.fork.messenger.recipients.RecipientId

/**
 * Fragment argument for `SafetyNumberBottomSheetFragment`
 */
@Parcelize
data class SafetyNumberBottomSheetArgs(
  val untrustedRecipients: List<RecipientId>,
  val destinations: List<ContactSearchKey.RecipientSearchKey>,
  val messageId: MessageId? = null
) : Parcelable
