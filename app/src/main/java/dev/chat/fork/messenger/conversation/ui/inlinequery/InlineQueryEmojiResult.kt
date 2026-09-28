package dev.chat.fork.messenger.conversation.ui.inlinequery

import android.view.View
import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.components.emoji.EmojiImageView
import dev.chat.fork.messenger.util.adapter.mapping.AnyMappingModel
import dev.chat.fork.messenger.util.adapter.mapping.MappingModel
import dev.chat.fork.messenger.util.adapter.mapping.MappingViewHolder

/**
 * Used to render inline emoji search results in a [dev.chat.fork.messenger.util.adapter.mapping.MappingAdapter]
 */
object InlineQueryEmojiResult {

  class Model(val canonicalEmoji: String, val preferredEmoji: String) : MappingModel<Model> {
    override fun areItemsTheSame(newItem: Model): Boolean {
      return canonicalEmoji == newItem.canonicalEmoji
    }

    override fun areContentsTheSame(newItem: Model): Boolean {
      return preferredEmoji == newItem.preferredEmoji
    }
  }

  class ViewHolder(itemView: View, private val listener: (AnyMappingModel) -> Unit) : MappingViewHolder<Model>(itemView) {

    private val emoji: EmojiImageView = findViewById(R.id.inline_query_emoji_image)

    override fun bind(model: Model) {
      itemView.setOnClickListener { listener(model) }
      emoji.setImageEmoji(model.preferredEmoji)
    }
  }
}
