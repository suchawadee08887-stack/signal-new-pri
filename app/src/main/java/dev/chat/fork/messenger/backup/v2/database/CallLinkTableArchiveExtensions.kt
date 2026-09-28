/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.backup.v2.database

import org.signal.core.util.select
import dev.chat.fork.messenger.database.CallLinkTable

fun CallLinkTable.getCallLinksForBackup(): CallLinkArchiveExporter {
  val cursor = readableDatabase
    .select()
    .from(CallLinkTable.TABLE_NAME)
    .where("${CallLinkTable.ROOT_KEY} NOT NULL")
    .run()

  return CallLinkArchiveExporter(cursor)
}
