/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.database.helpers.migration

import android.app.Application
import dev.chat.fork.messenger.database.SQLiteDatabase

/**
 * Because getting the color is a simple modulo operation, there is no need to store it in the database.
 */
@Suppress("ClassName")
object V197_DropAvatarColorFromCallLinks : SignalDatabaseMigration {
  override fun migrate(context: Application, db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    db.execSQL("ALTER TABLE call_link DROP COLUMN avatar_color")
  }
}
