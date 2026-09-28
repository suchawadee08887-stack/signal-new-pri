/*
 * Copyright 2025 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.main

import dev.chat.fork.messenger.components.snackbars.SnackbarHostKey

sealed interface MainSnackbarHostKey : SnackbarHostKey {
  data object Chat : MainSnackbarHostKey
  data object MainChrome : MainSnackbarHostKey
}
