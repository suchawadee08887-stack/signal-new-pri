package dev.chat.fork.messenger.stickers;

import androidx.annotation.NonNull;

import org.signal.core.models.database.StickerRecord;

public interface StickerEventListener {
  void onStickerSelected(@NonNull StickerRecord sticker);

  void onStickerManagementClicked();
}
