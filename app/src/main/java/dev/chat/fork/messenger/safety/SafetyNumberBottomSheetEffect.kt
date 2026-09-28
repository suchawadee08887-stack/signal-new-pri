package dev.chat.fork.messenger.safety

import dev.chat.fork.messenger.contacts.paged.ContactSearchKey
import dev.chat.fork.messenger.conversation.ui.error.TrustAndVerifyResult

/** One-shot side effects emitted by [SafetyNumberBottomSheetViewModel] for the fragment to handle. */
sealed interface SafetyNumberBottomSheetEffect {
  /**
   * The trust-and-verify operation finished. The fragment should inspect [result],
   * fire the appropriate [SafetyNumberBottomSheet.Callbacks] method, then dismiss.
   */
  data class TrustCompleted(
    val result: TrustAndVerifyResult,
    val destinations: List<ContactSearchKey.RecipientSearchKey>
  ) : SafetyNumberBottomSheetEffect
}
