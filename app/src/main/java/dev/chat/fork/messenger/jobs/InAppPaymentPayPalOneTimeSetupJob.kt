/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.jobs

import org.signal.donations.InAppPaymentType
import org.signal.donations.PaymentSource
import dev.chat.fork.messenger.components.settings.app.subscription.DonationSerializationHelper.toFiatMoney
import dev.chat.fork.messenger.components.settings.app.subscription.OneTimeInAppPaymentRepository
import dev.chat.fork.messenger.components.settings.app.subscription.PayPalRepository
import dev.chat.fork.messenger.components.settings.app.subscription.donate.paypal.PayPalConfirmationResult
import dev.chat.fork.messenger.database.InAppPaymentTable
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.jobmanager.Job
import dev.chat.fork.messenger.jobs.protos.InAppPaymentSetupJobData
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.recipients.RecipientId
import org.whispersystems.signalservice.api.subscriptions.PayPalCreatePaymentIntentResponse

class InAppPaymentPayPalOneTimeSetupJob private constructor(data: InAppPaymentSetupJobData, parameters: Parameters) : InAppPaymentSetupJob(data, parameters) {

  companion object {
    const val KEY = "InAppPaymentPayPalOneTimeSetupJob"

    /**
     * Creates a new job for performing stripe recurring payment setup. Note that
     * we do not require network for this job, as if the network is not present, we
     * should treat that as an immediate error and fail the job.
     */
    fun create(
      inAppPayment: InAppPaymentTable.InAppPayment,
      paymentSource: PaymentSource
    ): InAppPaymentPayPalOneTimeSetupJob {
      return InAppPaymentPayPalOneTimeSetupJob(
        getJobData(inAppPayment, paymentSource),
        getParameters(inAppPayment)
      )
    }
  }

  private val payPalRepository = PayPalRepository(AppDependencies.donationsService)

  override fun performPreUserAction(inAppPayment: InAppPaymentTable.InAppPayment): RequiredUserAction {
    info("Beginning one-time payment pipeline.")
    val amount = inAppPayment.data.amount!!.toFiatMoney()
    val recipientId = inAppPayment.data.recipientId?.let { RecipientId.from(it) } ?: Recipient.self().id
    if (inAppPayment.type == InAppPaymentType.ONE_TIME_GIFT) {
      info("Verifying recipient $recipientId can receive gift.")
      OneTimeInAppPaymentRepository.verifyRecipientIsAllowedToReceiveAGiftSync(recipientId)
    }

    info("Creating one-time payment intent...")
    val response: PayPalCreatePaymentIntentResponse = payPalRepository.createOneTimePaymentIntent(
      amount = amount,
      badgeRecipient = recipientId,
      badgeLevel = inAppPayment.data.level
    )

    return RequiredUserAction.PayPalActionRequired(
      approvalUrl = response.approvalUrl,
      tokenOrPaymentId = response.paymentId
    )
  }

  override fun performPostUserAction(inAppPayment: InAppPaymentTable.InAppPayment): Result {
    val result = PayPalConfirmationResult(
      payerId = inAppPayment.data.payPalActionComplete!!.payerId,
      paymentId = inAppPayment.data.payPalActionComplete.paymentId.takeIf { it.isNotBlank() },
      paymentToken = inAppPayment.data.payPalActionComplete.paymentToken
    )

    info("Confirming payment intent...")
    val response = payPalRepository.confirmOneTimePaymentIntent(
      amount = inAppPayment.data.amount!!.toFiatMoney(),
      badgeLevel = inAppPayment.data.level,
      paypalConfirmationResult = result
    )

    info("Confirmed payment intent. Submitting redemption job chain.")
    OneTimeInAppPaymentRepository.submitRedemptionJobChain(inAppPayment, response.paymentId)

    return Result.success()
  }

  override fun getFactoryKey(): String = KEY

  override fun run(): Result {
    return performTransaction()
  }

  class Factory : Job.Factory<InAppPaymentPayPalOneTimeSetupJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): InAppPaymentPayPalOneTimeSetupJob {
      val data = serializedData?.let { InAppPaymentSetupJobData.ADAPTER.decode(it) } ?: error("Missing job data!")

      return InAppPaymentPayPalOneTimeSetupJob(data, parameters)
    }
  }
}
