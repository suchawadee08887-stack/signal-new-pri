package dev.chat.fork.messenger.groups.ui.invitesandrequests.joining;

import dev.chat.fork.messenger.recipients.Recipient;

final class JoinGroupSuccess {
  private final Recipient groupRecipient;
  private final long      groupThreadId;

  JoinGroupSuccess(Recipient groupRecipient, long groupThreadId) {
    this.groupRecipient = groupRecipient;
    this.groupThreadId  = groupThreadId;
  }

  Recipient getGroupRecipient() {
    return groupRecipient;
  }

  long getGroupThreadId() {
    return groupThreadId;
  }
}
