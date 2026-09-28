package dev.chat.fork.messenger.components.settings.app.subscription.donate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import dev.chat.fork.messenger.database.InAppPaymentTable

@Parcelize
class InAppPaymentProcessorActionResult(
  val action: InAppPaymentProcessorAction,
  val inAppPaymentId: InAppPaymentTable.InAppPaymentId?,
  val status: Status
) : Parcelable {
  enum class Status {
    SUCCESS,
    FAILURE
  }
}
