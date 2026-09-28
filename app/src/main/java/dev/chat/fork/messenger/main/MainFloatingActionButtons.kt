/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.signal.core.ui.compose.DayNightPreviews
import org.signal.core.ui.compose.Previews
import org.signal.core.ui.compose.SignalIcons
import org.signal.core.ui.compose.theme.Dimensions
import org.signal.core.ui.compose.theme.SignalTheme
import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.window.NavigationType
import kotlin.math.roundToInt
import org.signal.core.ui.R as CoreUiR

private val ACTION_BUTTON_SIZE = Dimensions.fabSize
private val ACTION_BUTTON_SPACING = Dimensions.fabSpacing
private val ACTION_BUTTON_RADIUS = Dimensions.radiusLarge

interface MainFloatingActionButtonsCallback {
  fun onNewChatClick()
  fun onNewCallClick()
  fun onCameraClick(destination: MainNavigationListLocation)

  object Empty : MainFloatingActionButtonsCallback {
    override fun onNewChatClick() = Unit
    override fun onNewCallClick() = Unit
    override fun onCameraClick(destination: MainNavigationListLocation) = Unit
  }
}

@Composable
fun MainFloatingActionButtons(
  destination: MainNavigationListLocation,
  callback: MainFloatingActionButtonsCallback,
  modifier: Modifier = Modifier,
  navigationType: NavigationType = NavigationType.rememberNavigationType()
) {
  val boxHeightDp = (ACTION_BUTTON_SIZE * 2 + ACTION_BUTTON_SPACING)
  val boxHeightPx = with(LocalDensity.current) {
    boxHeightDp.toPx().roundToInt()
  }

  val primaryButtonAlignment = remember(navigationType) {
    when (navigationType) {
      NavigationType.RAIL -> Alignment.TopCenter
      NavigationType.BAR -> Alignment.BottomCenter
    }
  }

  val shadowElevation: Dp = remember(navigationType) {
    when (navigationType) {
      NavigationType.RAIL -> Dimensions.elevationNone
      NavigationType.BAR -> Dimensions.elevationMedium
    }
  }

  Box(
    modifier = modifier
      .padding(ACTION_BUTTON_SPACING)
      .height(boxHeightDp)
  ) {
    SecondaryActionButton(
      destination = destination,
      boxHeightPx = boxHeightPx,
      onCameraClick = callback::onCameraClick,
      elevation = shadowElevation
    )

    if (destination != MainNavigationListLocation.SETTINGS && destination != MainNavigationListLocation.PROFILE) {
      Box(
        modifier = Modifier.align(primaryButtonAlignment)
      ) {
        PrimaryActionButton(
          destination = destination,
          onNewChatClick = callback::onNewChatClick,
          onCameraClick = callback::onCameraClick,
          onNewCallClick = callback::onNewCallClick,
          elevation = shadowElevation
        )
      }
    }
  }
}

@Composable
private fun BoxScope.SecondaryActionButton(
  destination: MainNavigationListLocation,
  boxHeightPx: Int,
  elevation: Dp,
  onCameraClick: (MainNavigationListLocation) -> Unit
) {
  val navigationType = NavigationType.rememberNavigationType()
  val secondaryButtonAlignment = remember(navigationType) {
    when (navigationType) {
      NavigationType.RAIL -> Alignment.BottomCenter
      NavigationType.BAR -> Alignment.TopCenter
    }
  }

  val offsetYProvider: (Int) -> Int = remember(navigationType) {
    when (navigationType) {
      NavigationType.RAIL -> {
        { it - boxHeightPx }
      }
      NavigationType.BAR -> {
        { boxHeightPx - it }
      }
    }
  }

  AnimatedVisibility(
    visible = destination == MainNavigationListLocation.CHATS || destination == MainNavigationListLocation.ARCHIVE,
    modifier = Modifier.align(secondaryButtonAlignment),
    enter = slideInVertically(initialOffsetY = offsetYProvider),
    exit = slideOutVertically(targetOffsetY = offsetYProvider)
  ) {
    val animatedElevation by transition.animateDp(targetValueByState = { if (it == EnterExitState.Visible) elevation else 0.dp })

    CameraButton(
      colors = IconButtonDefaults.filledTonalIconButtonColors().copy(
        containerColor = when (navigationType) {
          NavigationType.RAIL -> MaterialTheme.colorScheme.surface
          NavigationType.BAR -> SignalTheme.colors.colorSurface2
        },
        contentColor = MaterialTheme.colorScheme.onSurface
      ),
      onClick = {
        onCameraClick(MainNavigationListLocation.CHATS)
      },
      shadowElevation = animatedElevation
    )
  }
}

@Composable
private fun PrimaryActionButton(
  destination: MainNavigationListLocation,
  elevation: Dp,
  onNewChatClick: () -> Unit = {},
  onCameraClick: (MainNavigationListLocation) -> Unit = {},
  onNewCallClick: () -> Unit = {}
) {
  val onClick = remember(destination) {
    when (destination) {
      MainNavigationListLocation.ARCHIVE -> onNewChatClick
      MainNavigationListLocation.CHATS -> onNewChatClick
      MainNavigationListLocation.CALLS -> onNewCallClick
      MainNavigationListLocation.STORIES -> {
        { onCameraClick(destination) }
      }
      MainNavigationListLocation.SETTINGS -> { {} }
      MainNavigationListLocation.PROFILE -> { {} }
    }
  }

  MainFloatingActionButton(
    onClick = onClick,
    shadowElevation = elevation,
    icon = {
      AnimatedContent(destination) { targetState ->
        val (icon, contentDescriptionId) = when (targetState) {
          MainNavigationListLocation.ARCHIVE -> CoreUiR.drawable.symbol_edit_24 to R.string.conversation_list_fragment__fab_content_description
          MainNavigationListLocation.CHATS -> CoreUiR.drawable.symbol_edit_24 to R.string.conversation_list_fragment__fab_content_description
          MainNavigationListLocation.CALLS -> R.drawable.symbol_phone_plus_24 to R.string.CallLogFragment__start_a_new_call
          MainNavigationListLocation.STORIES -> CoreUiR.drawable.symbol_camera_24 to R.string.conversation_list_fragment__open_camera_description
          MainNavigationListLocation.SETTINGS -> CoreUiR.drawable.symbol_edit_24 to R.string.conversation_list_fragment__fab_content_description
          MainNavigationListLocation.PROFILE -> CoreUiR.drawable.symbol_edit_24 to R.string.conversation_list_fragment__fab_content_description
        }

        Icon(
          imageVector = ImageVector.vectorResource(icon),
          contentDescription = stringResource(contentDescriptionId)
        )
      }
    }
  )
}

@Composable
private fun CameraButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  shadowElevation: Dp = Dimensions.elevationMedium,
  colors: IconButtonColors = IconButtonDefaults.filledTonalIconButtonColors()
) {
  MainFloatingActionButton(
    onClick = onClick,
    icon = {
      Icon(
        imageVector = SignalIcons.Camera.imageVector,
        contentDescription = stringResource(R.string.conversation_list_fragment__open_camera_description)
      )
    },
    colors = colors,
    modifier = modifier,
    shadowElevation = shadowElevation
  )
}

@Composable
private fun MainFloatingActionButton(
  onClick: () -> Unit,
  icon: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  shadowElevation: Dp = Dimensions.elevationMedium,
  colors: IconButtonColors = IconButtonDefaults.filledTonalIconButtonColors()
) {
  val shape = RoundedCornerShape(ACTION_BUTTON_RADIUS)
  FilledTonalIconButton(
    onClick = onClick,
    shape = shape,
    modifier = modifier
      .size(ACTION_BUTTON_SIZE)
      .shadow(shadowElevation, shape),
    enabled = true,
    colors = colors
  ) {
    icon()
  }
}

@DayNightPreviews
@Composable
private fun MainFloatingActionButtonsNavigationRailPreview() {
  var currentDestination by remember { mutableStateOf(MainNavigationListLocation.CHATS) }
  val callback = remember {
    object : MainFloatingActionButtonsCallback {
      override fun onCameraClick(destination: MainNavigationListLocation) {
        currentDestination = MainNavigationListLocation.CALLS
      }

      override fun onNewChatClick() {
        currentDestination = MainNavigationListLocation.STORIES
      }

      override fun onNewCallClick() {
        currentDestination = MainNavigationListLocation.CHATS
      }
    }
  }

  Previews.Preview {
    MainFloatingActionButtons(
      destination = currentDestination,
      callback = callback,
      navigationType = NavigationType.RAIL
    )
  }
}

@DayNightPreviews
@Composable
private fun MainFloatingActionButtonsNavigationBarPreview() {
  var currentDestination by remember { mutableStateOf(MainNavigationListLocation.CHATS) }
  val callback = remember {
    object : MainFloatingActionButtonsCallback {
      override fun onCameraClick(destination: MainNavigationListLocation) {
        currentDestination = MainNavigationListLocation.CALLS
      }

      override fun onNewChatClick() {
        currentDestination = MainNavigationListLocation.STORIES
      }

      override fun onNewCallClick() {
        currentDestination = MainNavigationListLocation.CHATS
      }
    }
  }

  Previews.Preview {
    MainFloatingActionButtons(
      destination = currentDestination,
      callback = callback,
      navigationType = NavigationType.BAR
    )
  }
}
