/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.jobmanager.impl

import android.app.job.JobInfo
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.jobmanager.Constraint
import dev.chat.fork.messenger.jobmanager.ConstraintObserver
import dev.chat.fork.messenger.jobs.StickerDownloadJob
import dev.chat.fork.messenger.jobs.StickerPackDownloadJob

/**
 * When met, no sticker download jobs should be in the job queue/running.
 */
object StickersNotDownloadingConstraint : Constraint {

  const val KEY = "StickersNotDownloadingConstraint"

  private val factoryKeys = setOf(StickerPackDownloadJob.KEY, StickerDownloadJob.KEY)

  override fun isMet(): Boolean {
    return AppDependencies.jobManager.areFactoriesEmpty(factoryKeys)
  }

  override fun getFactoryKey(): String = KEY

  override fun applyToJobInfo(jobInfoBuilder: JobInfo.Builder) = Unit

  object Observer : ConstraintObserver {
    override fun register(notifier: ConstraintObserver.Notifier) {
      AppDependencies.jobManager.addListener({ job -> factoryKeys.contains(job.factoryKey) }) { job, jobState ->
        if (jobState.isComplete) {
          if (isMet) {
            notifier.onConstraintMet(KEY)
          }
        }
      }
    }
  }

  class Factory : Constraint.Factory<StickersNotDownloadingConstraint> {
    override fun create(): StickersNotDownloadingConstraint {
      return StickersNotDownloadingConstraint
    }
  }
}
