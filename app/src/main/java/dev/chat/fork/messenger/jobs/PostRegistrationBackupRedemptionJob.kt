/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.jobs

import org.signal.core.util.logging.Log
import org.signal.core.util.money.FiatMoney
import org.signal.donations.InAppPaymentType
import dev.chat.fork.messenger.backup.DeletionState
import dev.chat.fork.messenger.backup.v2.MessageBackupTier
import dev.chat.fork.messenger.components.settings.app.subscription.DonationSerializationHelper.toFiatValue
import dev.chat.fork.messenger.components.settings.app.subscription.InAppPaymentsRepository
import dev.chat.fork.messenger.components.settings.app.subscription.RecurringInAppPaymentRepository
import dev.chat.fork.messenger.database.InAppPaymentTable
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.database.model.InAppPaymentSubscriberRecord
import dev.chat.fork.messenger.database.model.databaseprotos.InAppPaymentData
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.jobmanager.CoroutineJob
import dev.chat.fork.messenger.jobmanager.Job
import dev.chat.fork.messenger.jobmanager.impl.NetworkConstraint
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.recipients.Recipient
import org.whispersystems.signalservice.internal.push.SubscriptionsConfiguration
import java.math.BigDecimal
import java.util.Currency
import kotlin.concurrent.withLock

/**
 * Runs after registration to make sure we are on the backup level we expect on this device.
 */
class PostRegistrationBackupRedemptionJob : CoroutineJob {

  companion object {
    private val TAG = Log.tag(PostRegistrationBackupRedemptionJob::class)
    const val KEY = "PostRestoreBackupRedemptionJob"
  }

  constructor() : super(
    Parameters.Builder()
      .setQueue(InAppPaymentsRepository.getRecurringJobQueueKey(InAppPaymentType.RECURRING_BACKUP))
      .addConstraint(NetworkConstraint.KEY)
      .setMaxAttempts(Parameters.UNLIMITED)
      .setLifespan(Parameters.IMMORTAL)
      .build()
  )

  constructor(parameters: Parameters) : super(parameters)

  override fun serialize(): ByteArray? = null

  override fun getFactoryKey(): String = KEY

  override suspend fun doRun(): Result {
    if (!SignalStore.account.isRegistered) {
      info("User is not registered. Exiting.")
      return Result.success()
    }

    if (SignalStore.account.isLinkedDevice) {
      info("Linked device. Exiting.")
      return Result.success()
    }

    if (SignalStore.backup.deletionState != DeletionState.NONE) {
      info("User is in the process of or has delete their backup. Exiting.")
      return Result.success()
    }

    if (SignalStore.backup.backupTier != MessageBackupTier.PAID) {
      info("Paid backups are not enabled on this device. Exiting.")
      return Result.success()
    }

    if (SignalStore.backup.backupTierInternalOverride != null) {
      info("User has internal override set for backup version. Exiting.")
      return Result.success()
    }

    if (SignalDatabase.inAppPayments.hasPendingBackupRedemption()) {
      info("User has a pending backup redemption. Retrying later.")
      return Result.retry(defaultBackoff())
    }

    val subscriber = InAppPaymentsRepository.getSubscriber(InAppPaymentSubscriberRecord.Type.BACKUP)
    if (subscriber == null) {
      info("No subscriber information was available in the database. Exiting.")
      return Result.success()
    }

    info("Attempting to grab price information for records...")
    val subscription = RecurringInAppPaymentRepository.getActiveSubscriptionSync(InAppPaymentSubscriberRecord.Type.BACKUP).successOrNull()?.activeSubscription

    val emptyPrice = FiatMoney(BigDecimal.ZERO, SignalStore.inAppPayments.getOneTimeCurrency())
    val price: FiatMoney = if (subscription != null) {
      FiatMoney.fromSignalNetworkAmount(subscription.amount, Currency.getInstance(subscription.currency))
    } else if (AppDependencies.billingApi.getApiAvailability().isSuccess) {
      AppDependencies.billingApi.queryProduct()?.price ?: emptyPrice
    } else {
      emptyPrice
    }

    if (price == emptyPrice) {
      warning("Could not resolve price, using empty price.")
    }

    InAppPaymentSubscriberRecord.Type.BACKUP.lock.withLock {
      if (SignalDatabase.inAppPayments.hasPendingBackupRedemption()) {
        warning("Backup is already pending redemption. Exiting.")
        return Result.success()
      }

      info("Creating a pending payment...")
      val id = SignalDatabase.inAppPayments.insert(
        type = InAppPaymentType.RECURRING_BACKUP,
        state = InAppPaymentTable.State.PENDING,
        subscriberId = InAppPaymentsRepository.requireSubscriber(InAppPaymentSubscriberRecord.Type.BACKUP).subscriberId,
        endOfPeriod = null,
        inAppPaymentData = InAppPaymentData(
          badge = null,
          amount = price.toFiatValue(),
          level = SubscriptionsConfiguration.BACKUPS_LEVEL.toLong(),
          recipientId = Recipient.self().id.serialize(),
          paymentMethodType = InAppPaymentData.PaymentMethodType.GOOGLE_PLAY_BILLING,
          redemption = InAppPaymentData.RedemptionState(
            stage = InAppPaymentData.RedemptionState.Stage.INIT
          )
        )
      )

      info("Submitting job chain.")
      InAppPaymentPurchaseTokenJob.createJobChain(
        inAppPayment = SignalDatabase.inAppPayments.getById(id)!!
      ).enqueue()
    }

    return Result.success()
  }

  override fun onFailure() = Unit

  private fun info(message: String, throwable: Throwable? = null) {
    Log.i(TAG, message, throwable, true)
  }

  private fun warning(message: String, throwable: Throwable? = null) {
    Log.w(TAG, message, throwable, true)
  }

  class Factory : Job.Factory<PostRegistrationBackupRedemptionJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): PostRegistrationBackupRedemptionJob {
      return PostRegistrationBackupRedemptionJob(parameters)
    }
  }
}
