package dev.chat.fork.messenger.components.settings.app.subscription.donate.paypal

import android.content.DialogInterface
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.signal.core.util.concurrent.SignalDispatchers
import org.signal.core.util.dp
import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.badges.Badges
import dev.chat.fork.messenger.badges.models.BadgeDisplay112
import dev.chat.fork.messenger.components.settings.DSLConfiguration
import dev.chat.fork.messenger.components.settings.DSLSettingsAdapter
import dev.chat.fork.messenger.components.settings.DSLSettingsBottomSheetFragment
import dev.chat.fork.messenger.components.settings.DSLSettingsText
import dev.chat.fork.messenger.components.settings.app.subscription.donate.gateway.GatewaySelectorBottomSheet.Companion.presentTitleAndSubtitle
import dev.chat.fork.messenger.components.settings.configure
import dev.chat.fork.messenger.database.InAppPaymentTable
import dev.chat.fork.messenger.database.SignalDatabase

/**
 * Bottom sheet for final order confirmation from PayPal
 */
class PayPalCompleteOrderBottomSheet : DSLSettingsBottomSheetFragment() {

  companion object {
    const val REQUEST_KEY = "complete_order"
  }

  private var didConfirmOrder = false
  private val args: PayPalCompleteOrderBottomSheetArgs by navArgs()

  override fun bindAdapter(adapter: DSLSettingsAdapter) {
    BadgeDisplay112.register(adapter)
    PayPalCompleteOrderPaymentItem.register(adapter)

    lifecycleScope.launch {
      val inAppPayment = withContext(SignalDispatchers.Default) {
        SignalDatabase.inAppPayments.getById(args.inAppPaymentId)!!
      }

      adapter.submitList(getConfiguration(inAppPayment).toMappingModelList())
    }
  }

  override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)
    setFragmentResult(REQUEST_KEY, bundleOf(REQUEST_KEY to didConfirmOrder))
  }

  private fun getConfiguration(inAppPayment: InAppPaymentTable.InAppPayment): DSLConfiguration {
    return configure {
      customPref(
        BadgeDisplay112.Model(
          badge = Badges.fromDatabaseBadge(inAppPayment.data.badge!!),
          withDisplayText = false
        )
      )

      space(12.dp)

      presentTitleAndSubtitle(requireContext(), inAppPayment)

      space(24.dp)

      customPref(PayPalCompleteOrderPaymentItem.Model())

      space(82.dp)

      primaryButton(
        text = DSLSettingsText.from(R.string.PaypalCompleteOrderBottomSheet__donate),
        onClick = {
          didConfirmOrder = true
          findNavController().popBackStack()
        }
      )

      secondaryButtonNoOutline(
        text = DSLSettingsText.from(android.R.string.cancel),
        onClick = {
          findNavController().popBackStack()
        }
      )

      space(16.dp)
    }
  }
}
