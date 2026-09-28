package dev.chat.fork.messenger.conversation.colors.ui

import androidx.lifecycle.LiveData
import org.signal.core.util.concurrent.SignalExecutors
import dev.chat.fork.messenger.conversation.colors.ChatColors
import dev.chat.fork.messenger.conversation.colors.ChatColorsPalette
import dev.chat.fork.messenger.database.ChatColorsTable
import dev.chat.fork.messenger.database.DatabaseObserver
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.util.concurrent.SerialMonoLifoExecutor
import java.util.concurrent.Executor

class ChatColorsOptionsLiveData : LiveData<List<ChatColors>>() {
  private val chatColorsTable: ChatColorsTable = SignalDatabase.chatColors
  private val observer: DatabaseObserver.Observer = DatabaseObserver.Observer { refreshChatColors() }
  private val executor: Executor = SerialMonoLifoExecutor(SignalExecutors.BOUNDED)

  override fun onActive() {
    refreshChatColors()
    AppDependencies.databaseObserver.registerChatColorsObserver(observer)
  }

  override fun onInactive() {
    AppDependencies.databaseObserver.unregisterObserver(observer)
  }

  private fun refreshChatColors() {
    executor.execute {
      val options = mutableListOf<ChatColors>().apply {
        addAll(ChatColorsPalette.Bubbles.all)
        addAll(chatColorsTable.getSavedChatColors())
      }

      postValue(options)
    }
  }
}
