/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.logsubmit

import android.content.Context
import dev.chat.fork.messenger.database.LogDatabase
import dev.chat.fork.messenger.dependencies.AppDependencies
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogSectionAnr : LogSection {

  companion object {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz", Locale.US)
  }

  override fun getTitle(): String = "ANR"

  override fun getContent(context: Context): CharSequence {
    val anrs = LogDatabase.getInstance(AppDependencies.application).anrs.getAll()

    return if (anrs.isEmpty()) {
      "None"
    } else {
      "\n" + anrs.joinToString(separator = "\n\n") {
        val date = dateFormat.format(Date(it.createdAt))
        "------------- $date -------------\n${it.threadDump}"
      }
    }
  }
}
