/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.database.helpers.migration

import android.app.Application
import dev.chat.fork.messenger.database.SQLiteDatabase

/**
 * Adds a phone_number_sharing column to the recipient table.
 */
@Suppress("ClassName")
object V214_PhoneNumberSharingColumn : SignalDatabaseMigration {
  override fun migrate(context: Application, db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    db.execSQL("ALTER TABLE recipient ADD COLUMN phone_number_sharing INTEGER DEFAULT 0")
  }
}
