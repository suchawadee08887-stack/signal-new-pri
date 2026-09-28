package dev.chat.fork.messenger.conversation.ui.mentions;

import androidx.annotation.NonNull;

import dev.chat.fork.messenger.recipients.Recipient;
import dev.chat.fork.messenger.util.viewholders.RecipientMappingModel;

public final class MentionViewState extends RecipientMappingModel<MentionViewState> {

  private final Recipient recipient;

  public MentionViewState(@NonNull Recipient recipient) {
    this.recipient = recipient;
  }

  @Override
  public @NonNull Recipient getRecipient() {
    return recipient;
  }
}
