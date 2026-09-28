package dev.chat.fork.messenger.keyboard.emoji

import dev.chat.fork.messenger.components.emoji.EmojiEventListener
import dev.chat.fork.messenger.keyboard.emoji.search.EmojiSearchFragment

interface EmojiKeyboardCallback :
  EmojiEventListener,
  EmojiKeyboardPageFragment.Callback,
  EmojiSearchFragment.Callback
