package dev.chat.fork.messenger.giph.mp4;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dev.chat.fork.messenger.R;
import dev.chat.fork.messenger.giph.model.GiphyImage;
import dev.chat.fork.messenger.util.adapter.mapping.LayoutFactory;
import dev.chat.fork.messenger.util.adapter.mapping.PagingMappingAdapter;

/**
 * Maintains and displays a list of GiphyImage objects. This Adapter always displays gifs
 * as MP4 videos.
 */
final class GiphyMp4Adapter extends PagingMappingAdapter<String> {
  public GiphyMp4Adapter(@Nullable Callback listener) {
    registerFactory(GiphyImage.class, new LayoutFactory<>(v -> new GiphyMp4ViewHolder(v, listener), R.layout.giphy_mp4));
  }

  interface Callback {
    void onClick(@NonNull GiphyImage giphyImage);
  }
}