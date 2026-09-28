package dev.chat.fork.messenger.keyboard.emoji

import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.keyboard.KeyboardPageCategoryIconViewHolder
import dev.chat.fork.messenger.util.adapter.mapping.LayoutFactory
import dev.chat.fork.messenger.util.adapter.mapping.MappingAdapter
import java.util.function.Consumer

class EmojiKeyboardPageCategoriesAdapter(private val onPageSelected: Consumer<String>) : MappingAdapter() {
  init {
    registerFactory(RecentsMappingModel::class.java, LayoutFactory({ v -> KeyboardPageCategoryIconViewHolder(v, onPageSelected) }, R.layout.keyboard_pager_category_icon))
    registerFactory(EmojiCategoryMappingModel::class.java, LayoutFactory({ v -> KeyboardPageCategoryIconViewHolder(v, onPageSelected) }, R.layout.keyboard_pager_category_icon))
  }
}
