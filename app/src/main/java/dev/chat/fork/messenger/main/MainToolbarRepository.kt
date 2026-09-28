/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.main

import org.signal.core.util.concurrent.SignalExecutors
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.notifications.MarkReadReceiver

object MainToolbarRepository {
  /**
   * Mark all unread messages in the local database as read.
   */
  fun markAllMessagesRead() {
    SignalExecutors.BOUNDED.execute {
      val messageIds = SignalDatabase.threads.setAllThreadsRead()
      AppDependencies.messageNotifier.updateNotification(AppDependencies.application)
      MarkReadReceiver.process(messageIds)
    }
  }
}
