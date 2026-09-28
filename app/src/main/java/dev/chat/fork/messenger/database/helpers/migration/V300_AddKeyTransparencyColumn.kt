package dev.chat.fork.messenger.database.helpers.migration

import android.app.Application
import dev.chat.fork.messenger.database.SQLiteDatabase

/**
 * Adds column to recipient to track key transparency data
 */
@Suppress("ClassName")
object V300_AddKeyTransparencyColumn : SignalDatabaseMigration {

  override fun migrate(context: Application, db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    db.execSQL("ALTER TABLE recipient ADD COLUMN key_transparency_data BLOB DEFAULT NULL")
  }
}
