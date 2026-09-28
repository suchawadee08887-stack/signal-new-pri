/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.core.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.signal.core.ui.compose.theme.Dimensions
import org.signal.core.ui.compose.theme.SignalTheme

/**
 * Shared empty-state layouts for list and search surfaces.
 */
object EmptyStates {

  /**
   * Centered empty state with optional leading icon, title, and supporting body.
   * Does not change navigation or actions — callers supply optional [action] content.
   */
  @Composable
  fun Simple(
    title: String,
    modifier: Modifier = Modifier,
    body: String? = null,
    icon: ImageVector? = null,
    iconContentDescription: String? = null,
    action: @Composable (() -> Unit)? = null
  ) {
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(horizontal = Dimensions.gutterWide, vertical = Dimensions.space8),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      if (icon != null) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(72.dp)
            .background(color = SignalTheme.colors.colorSurface2, shape = CircleShape)
        ) {
          Icon(
            imageVector = icon,
            contentDescription = iconContentDescription,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(Dimensions.iconSize)
          )
        }
        Spacer(modifier = Modifier.height(Dimensions.space5))
      }

      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )

      if (body != null) {
        Spacer(modifier = Modifier.height(Dimensions.space2))
        Text(
          text = body,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth()
        )
      }

      if (action != null) {
        Spacer(modifier = Modifier.height(Dimensions.space5))
        action()
      }
    }
  }
}

@DayNightPreviews
@Composable
private fun EmptyStatePreview() {
  Previews.Preview {
    EmptyStates.Simple(
      icon = SignalIcons.Search.imageVector,
      title = "No results",
      body = "Try a different name or number."
    )
  }
}
