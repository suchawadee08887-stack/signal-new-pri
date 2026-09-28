package dev.chat.fork.messenger.wallpaper;

import androidx.annotation.Nullable;

import dev.chat.fork.messenger.R;
import dev.chat.fork.messenger.util.adapter.mapping.MappingAdapter;

class ChatWallpaperSelectionAdapter extends MappingAdapter {
  ChatWallpaperSelectionAdapter(@Nullable ChatWallpaperViewHolder.EventListener eventListener) {
    registerFactory(ChatWallpaperSelectionMappingModel.class, ChatWallpaperViewHolder.createFactory(R.layout.chat_wallpaper_selection_fragment_adapter_item, eventListener, null));
  }
}
