package dev.chat.fork.messenger.jobs

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEmpty
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.signal.core.util.deleteAll
import org.signal.core.util.money.FiatMoney
import org.signal.donations.InAppPaymentType
import dev.chat.fork.messenger.components.settings.app.subscription.DonationSerializationHelper.toFiatValue
import dev.chat.fork.messenger.database.DonationReceiptTable
import dev.chat.fork.messenger.database.InAppPaymentTable
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.database.model.InAppPaymentReceiptRecord
import dev.chat.fork.messenger.database.model.databaseprotos.InAppPaymentData
import dev.chat.fork.messenger.testing.SignalActivityRule
import java.math.BigDecimal
import java.util.Currency

@RunWith(AndroidJUnit4::class)
class InAppPaymentAuthCheckJobTest {

  companion object {
    private const val TEST_INTENT_ID = "test-intent-id"
    private const val TEST_CLIENT_SECRET = "test-client-secret"
  }

  @get:Rule
  val harness = SignalActivityRule()

  @Before
  fun setUp() {
    SignalDatabase.inAppPayments.writableDatabase.deleteAll(InAppPaymentTable.TABLE_NAME)
    SignalDatabase.donationReceipts.writableDatabase.deleteAll(DonationReceiptTable.TABLE_NAME)
  }

  @Test
  fun givenCanceledOneTimeAuthRequiredPayment_whenICheck_thenIDoNotExpectAReceipt() {
    SignalDatabase.inAppPayments.insert(
      type = InAppPaymentType.ONE_TIME_DONATION,
      state = InAppPaymentTable.State.WAITING_FOR_AUTHORIZATION,
      subscriberId = null,
      endOfPeriod = null,
      inAppPaymentData = InAppPaymentData(
        amount = FiatMoney(BigDecimal.ONE, Currency.getInstance("USD")).toFiatValue(),
        waitForAuth = InAppPaymentData.WaitingForAuthorizationState(
          stripeIntentId = TEST_INTENT_ID,
          stripeClientSecret = TEST_CLIENT_SECRET
        )
      )
    )

    InAppPaymentAuthCheckJob().run()

    val receipts = SignalDatabase.donationReceipts.getReceipts(InAppPaymentReceiptRecord.Type.ONE_TIME_DONATION)
    assertThat(receipts).isEmpty()
  }
}
