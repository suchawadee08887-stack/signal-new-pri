package dev.chat.fork.messenger.database.helpers.migration

import android.app.Application
import dev.chat.fork.messenger.database.SQLiteDatabase

@Suppress("ClassName")
object V316_AddVerifiedGroupNameHashMigration : SignalDatabaseMigration {
  override fun migrate(context: Application, db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    db.execSQL("ALTER TABLE groups ADD COLUMN verified_name_hash BLOB DEFAULT NULL")
  }
}
