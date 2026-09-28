package dev.chat.fork.messenger.keyboard.emoji

import dev.chat.fork.messenger.components.emoji.EmojiPageModel
import dev.chat.fork.messenger.components.emoji.EmojiPageViewGridAdapter
import dev.chat.fork.messenger.components.emoji.RecentEmojiPageModel
import dev.chat.fork.messenger.components.emoji.parsing.EmojiTree
import dev.chat.fork.messenger.emoji.EmojiCategory
import dev.chat.fork.messenger.emoji.EmojiSource
import dev.chat.fork.messenger.util.adapter.mapping.MappingModel

fun EmojiPageModel.toMappingModels(): List<MappingModel<*>> {
  val emojiTree: EmojiTree = EmojiSource.latest.emojiTree

  return displayEmoji.map {
    val isTextEmoji = EmojiCategory.EMOTICONS.key == key || (RecentEmojiPageModel.KEY == key && emojiTree.getEmoji(it.value, 0, it.value.length) == null)

    if (isTextEmoji) {
      EmojiPageViewGridAdapter.EmojiTextModel(key, it)
    } else {
      EmojiPageViewGridAdapter.EmojiModel(key, it)
    }
  }
}
