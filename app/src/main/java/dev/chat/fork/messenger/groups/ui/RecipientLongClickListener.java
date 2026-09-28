package dev.chat.fork.messenger.groups.ui;

import androidx.annotation.NonNull;

import dev.chat.fork.messenger.recipients.Recipient;

public interface RecipientLongClickListener {
  boolean onLongClick(@NonNull Recipient recipient);
}
