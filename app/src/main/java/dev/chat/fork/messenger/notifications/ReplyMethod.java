package dev.chat.fork.messenger.notifications;

import androidx.annotation.NonNull;

import dev.chat.fork.messenger.recipients.Recipient;

public enum ReplyMethod {

  GroupMessage,
  SecureMessage;

  public static @NonNull ReplyMethod forRecipient(Recipient recipient) {
    if (recipient.isGroup()) {
      return ReplyMethod.GroupMessage;
    } else {
      return ReplyMethod.SecureMessage;
    }
  }
}
