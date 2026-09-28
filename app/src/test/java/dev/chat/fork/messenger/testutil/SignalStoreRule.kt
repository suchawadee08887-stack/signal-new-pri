/*
 * Copyright 2026 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.testutil

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import org.junit.rules.ExternalResource
import dev.chat.fork.messenger.keyvalue.KeyValueDataSet
import dev.chat.fork.messenger.keyvalue.KeyValueStore
import dev.chat.fork.messenger.keyvalue.MockKeyValuePersistentStorage
import dev.chat.fork.messenger.keyvalue.SignalStore

/**
 * Installs a real [SignalStore] backed by in-memory storage, letting tests arrange and assert on actual
 * stored state rather than stubbing interactions. Use [MockSignalStoreRule] instead when you need
 * interaction-based mocking.
 */
class SignalStoreRule : ExternalResource() {

  override fun before() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    SignalStore.testInject(SignalStore(application, KeyValueStore(MockKeyValuePersistentStorage.withDataSet(KeyValueDataSet()))))
  }

  override fun after() {
    SignalStore.testInject(null)
  }
}
