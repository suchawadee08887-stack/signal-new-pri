package dev.chat.fork.messenger.database.loaders;

import android.content.Context;

import dev.chat.fork.messenger.util.AbstractCursorLoader;

public abstract class MediaLoader extends AbstractCursorLoader {

  MediaLoader(Context context) {
    super(context);
  }

  public enum MediaType {
    GALLERY,
    DOCUMENT,
    AUDIO,
    LINK,
    ALL
  }
}
