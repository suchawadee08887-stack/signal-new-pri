package dev.chat.fork.messenger.components.emoji.parsing

import dev.chat.fork.messenger.emoji.EmojiPage

data class EmojiDrawInfo(val page: EmojiPage, val index: Int, val emoji: String, val rawEmoji: String?, val jumboSheet: String?)
