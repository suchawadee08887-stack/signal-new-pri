/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.backup.v2

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import dev.chat.fork.messenger.testing.SignalFlakyTest
import dev.chat.fork.messenger.testing.SignalFlakyTestRule

@RunWith(AndroidJUnit4::class)
class FlakyTestAnnotationTest {

  @get:Rule
  val flakyTestRule = SignalFlakyTestRule()

  companion object {
    private var count = 0
  }

  @SignalFlakyTest
  @Test
  fun purposelyFlaky() {
    count++
    assertEquals(3, count)
  }
}
