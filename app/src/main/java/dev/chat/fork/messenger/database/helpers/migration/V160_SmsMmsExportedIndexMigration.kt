package dev.chat.fork.messenger.database.helpers.migration

import android.app.Application
import dev.chat.fork.messenger.database.SQLiteDatabase

@Suppress("ClassName")
object V160_SmsMmsExportedIndexMigration : SignalDatabaseMigration {
  override fun migrate(context: Application, db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    db.execSQL("CREATE INDEX IF NOT EXISTS sms_exported_index ON sms (exported)")
    db.execSQL("CREATE INDEX IF NOT EXISTS mms_exported_index ON mms (exported)")
  }
}
