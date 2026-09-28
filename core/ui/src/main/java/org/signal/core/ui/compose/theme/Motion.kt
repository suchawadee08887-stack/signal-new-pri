/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.core.ui.compose.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween

/**
 * Shared motion tokens for short, purposeful UI transitions.
 * Prefer these durations so chrome animations feel consistent app-wide.
 */
object Motion {
  const val durationShort1: Int = 100
  const val durationShort2: Int = 150
  const val durationMedium1: Int = 200
  const val durationMedium2: Int = 250
  const val durationLong1: Int = 300

  val emphasized: Easing = FastOutSlowInEasing
  val standard: Easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

  fun <T> shortTween(delayMillis: Int = 0) = tween<T>(
    durationMillis = durationShort2,
    delayMillis = delayMillis,
    easing = standard
  )

  fun <T> mediumTween(delayMillis: Int = 0) = tween<T>(
    durationMillis = durationMedium1,
    delayMillis = delayMillis,
    easing = emphasized
  )

  fun <T> longTween(delayMillis: Int = 0) = tween<T>(
    durationMillis = durationLong1,
    delayMillis = delayMillis,
    easing = emphasized
  )
}
