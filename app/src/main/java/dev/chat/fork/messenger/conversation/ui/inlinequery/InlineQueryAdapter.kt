package dev.chat.fork.messenger.conversation.ui.inlinequery

import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.util.adapter.mapping.AnyMappingModel
import dev.chat.fork.messenger.util.adapter.mapping.MappingAdapter

class InlineQueryAdapter(listener: (AnyMappingModel) -> Unit) : MappingAdapter() {
  init {
    registerFactory(InlineQueryEmojiResult.Model::class.java, { InlineQueryEmojiResult.ViewHolder(it, listener) }, R.layout.inline_query_emoji_result)
  }
}
