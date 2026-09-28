/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.database.helpers.migration

import android.app.Application
import dev.chat.fork.messenger.database.SQLiteDatabase

/**
 * Adds necessary fields to the recipeints table for the nickname & notes feature.
 */
@Suppress("ClassName")
object V223_AddNicknameAndNoteFieldsToRecipientTable : SignalDatabaseMigration {
  override fun migrate(context: Application, db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    db.execSQL("ALTER TABLE recipient ADD COLUMN nickname_given_name TEXT DEFAULT NULL")
    db.execSQL("ALTER TABLE recipient ADD COLUMN nickname_family_name TEXT DEFAULT NULL")
    db.execSQL("ALTER TABLE recipient ADD COLUMN nickname_joined_name TEXT DEFAULT NULL")
    db.execSQL("ALTER TABLE recipient ADD COLUMN note TEXT DEFAULT NULL")
  }
}
