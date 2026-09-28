package dev.chat.fork.messenger.database.helpers.migration

import android.app.Application
import dev.chat.fork.messenger.database.SQLiteDatabase

@Suppress("ClassName")
object V159_ThreadUnreadSelfMentionCount : SignalDatabaseMigration {
  override fun migrate(context: Application, db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    db.execSQL("ALTER TABLE thread ADD COLUMN unread_self_mention_count INTEGER DEFAULT 0")
  }
}
