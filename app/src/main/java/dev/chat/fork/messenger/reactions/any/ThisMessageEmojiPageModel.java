package dev.chat.fork.messenger.reactions.any;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.stream.Collectors;

import dev.chat.fork.messenger.R;
import dev.chat.fork.messenger.components.emoji.Emoji;
import dev.chat.fork.messenger.components.emoji.EmojiPageModel;
import dev.chat.fork.messenger.components.emoji.RecentEmojiPageModel;

import java.util.List;

/**
 * Contains the Emojis that have been used in reactions for a given message.
 */
class ThisMessageEmojiPageModel implements EmojiPageModel {

  private final List<String> emoji;

  ThisMessageEmojiPageModel(@NonNull List<String> emoji) {
    this.emoji = emoji;
  }

  @Override
  public String getKey() {
    return RecentEmojiPageModel.KEY;
  }

  @Override
  public int getIconAttr() {
    return R.attr.emoji_category_recent;
  }

  @Override
  public @NonNull List<String> getEmoji() {
    return emoji;
  }

  @Override
  public @NonNull List<Emoji> getDisplayEmoji() {
    return getEmoji().stream().map(Emoji::new).collect(Collectors.toList());
  }

  @Override
  public @Nullable Uri getSpriteUri() {
    return null;
  }

  @Override
  public boolean isDynamic() {
    return true;
  }
}
