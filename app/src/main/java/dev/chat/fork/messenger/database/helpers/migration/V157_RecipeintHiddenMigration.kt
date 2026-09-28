package dev.chat.fork.messenger.database.helpers.migration

import android.app.Application
import dev.chat.fork.messenger.database.SQLiteDatabase

@Suppress("ClassName")
object V157_RecipeintHiddenMigration : SignalDatabaseMigration {
  override fun migrate(context: Application, db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    db.execSQL("ALTER TABLE recipient ADD COLUMN hidden INTEGER DEFAULT 0")
  }
}
