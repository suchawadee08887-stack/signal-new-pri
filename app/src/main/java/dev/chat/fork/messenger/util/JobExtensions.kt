/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.util

import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.jobmanager.Job
import dev.chat.fork.messenger.jobmanager.JobManager

/** Starts a new chain with this job. */
fun Job.asChain(): JobManager.Chain {
  return AppDependencies.jobManager.startChain(this)
}
