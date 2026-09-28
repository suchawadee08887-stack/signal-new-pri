package dev.chat.fork.messenger.groups.ui;

import androidx.annotation.NonNull;

import dev.chat.fork.messenger.recipients.Recipient;

public interface RecipientClickListener {
  void onClick(@NonNull Recipient recipient);
}
