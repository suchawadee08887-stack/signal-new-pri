/*
 * Copyright 2026 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.dependencies

import org.signal.camera.CameraDependencies
import dev.chat.fork.messenger.mms.TranscodingConfigProvider
import dev.chat.fork.messenger.stories.Stories

object CameraDependenciesProvider : CameraDependencies.Provider {
  override fun isStoriesFeatureEnabled(): Boolean {
    return Stories.isFeatureEnabled()
  }

  override fun getMaxVideoBitrateBps(): Int {
    return TranscodingConfigProvider.getMaxVideoBitrateBps()
  }
}
