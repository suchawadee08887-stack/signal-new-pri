/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.banner.banners

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.signal.core.ui.compose.DayNightPreviews
import org.signal.core.ui.compose.Previews
import org.signal.core.util.ServiceUtil
import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.banner.Banner
import dev.chat.fork.messenger.banner.ui.compose.Action
import dev.chat.fork.messenger.banner.ui.compose.DefaultBanner
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.util.PowerManagerCompat
import dev.chat.fork.messenger.util.TextSecurePreferences

class DozeBanner(private val context: Context, private val onDismissListener: () -> Unit) : Banner<Unit>() {

  override val enabled: Boolean
    get() = !SignalStore.account.fcmEnabled && !TextSecurePreferences.hasPromptedOptimizeDoze(context) && !ServiceUtil.getPowerManager(context).isIgnoringBatteryOptimizations(context.packageName)

  override val dataFlow: Flow<Unit>
    get() = flowOf(Unit)

  @Composable
  override fun DisplayBanner(model: Unit, contentPadding: PaddingValues) {
    Banner(
      contentPadding = contentPadding,
      onDismissListener = {
        TextSecurePreferences.setPromptedOptimizeDoze(context, true)
        onDismissListener.invoke()
      },
      onOkListener = {
        TextSecurePreferences.setPromptedOptimizeDoze(context, true)
        PowerManagerCompat.requestIgnoreBatteryOptimizations(context)
      }
    )
  }
}

@Composable
private fun Banner(contentPadding: PaddingValues, onDismissListener: () -> Unit = {}, onOkListener: () -> Unit = {}) {
  DefaultBanner(
    title = stringResource(id = R.string.DozeReminder_optimize_for_missing_play_services),
    body = stringResource(id = R.string.DozeReminder_this_device_does_not_support_play_services_tap_to_disable_system_battery),
    onDismissListener = onDismissListener,
    actions = listOf(
      Action(android.R.string.ok) {
        onOkListener()
      }
    ),
    paddingValues = contentPadding
  )
}

@DayNightPreviews
@Composable
private fun BannerPreview() {
  Previews.Preview {
    Banner(contentPadding = PaddingValues(0.dp))
  }
}
