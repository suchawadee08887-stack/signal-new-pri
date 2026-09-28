package dev.chat.fork.messenger.keyboard.emoji

import android.content.Context
import org.signal.core.util.concurrent.SignalExecutors
import dev.chat.fork.messenger.components.emoji.EmojiPageModel
import dev.chat.fork.messenger.components.emoji.RecentEmojiPageModel
import dev.chat.fork.messenger.emoji.EmojiSource.Companion.latest
import dev.chat.fork.messenger.util.TextSecurePreferences
import java.util.function.Consumer

class EmojiKeyboardPageRepository(private val context: Context) {
  fun getEmoji(consumer: Consumer<List<EmojiPageModel>>) {
    SignalExecutors.BOUNDED.execute {
      val list = mutableListOf<EmojiPageModel>()
      list += RecentEmojiPageModel(context, TextSecurePreferences.RECENT_STORAGE_KEY)
      list += latest.displayPages
      consumer.accept(list)
    }
  }
}
