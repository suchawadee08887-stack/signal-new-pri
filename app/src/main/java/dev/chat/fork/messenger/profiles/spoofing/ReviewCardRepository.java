package dev.chat.fork.messenger.profiles.spoofing;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import org.signal.core.util.concurrent.SignalExecutors;
import dev.chat.fork.messenger.database.SignalDatabase;
import dev.chat.fork.messenger.database.ThreadTable;
import dev.chat.fork.messenger.dependencies.AppDependencies;
import dev.chat.fork.messenger.groups.GroupChangeException;
import dev.chat.fork.messenger.groups.GroupId;
import dev.chat.fork.messenger.groups.GroupManager;
import dev.chat.fork.messenger.groups.GroupsInCommonRepository;
import dev.chat.fork.messenger.jobs.MultiDeviceMessageRequestResponseJob;
import dev.chat.fork.messenger.keyvalue.SignalStore;
import dev.chat.fork.messenger.recipients.Recipient;
import dev.chat.fork.messenger.recipients.RecipientId;
import dev.chat.fork.messenger.recipients.RecipientUtil;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

class ReviewCardRepository {

  private final Context     context;
  private final GroupId.V2  groupId;
  private final RecipientId recipientId;

  protected ReviewCardRepository(@NonNull Context context,
                                 @NonNull GroupId.V2 groupId)
  {
    this.context     = context;
    this.groupId     = groupId;
    this.recipientId = null;
  }

  protected ReviewCardRepository(@NonNull Context context,
                                 @NonNull RecipientId recipientId)
  {
    this.context     = context;
    this.groupId     = null;
    this.recipientId = recipientId;
  }

  void loadRecipients(@NonNull OnRecipientsLoadedListener onRecipientsLoadedListener) {
    if (groupId != null) {
      loadRecipientsForGroup(groupId, onRecipientsLoadedListener);
    } else if (recipientId != null) {
      loadSimilarRecipients(recipientId, onRecipientsLoadedListener);
    } else {
      throw new AssertionError();
    }
  }

  @WorkerThread
  int loadGroupsInCommonCount(@NonNull ReviewRecipient reviewRecipient) {
    return GroupsInCommonRepository.getGroupsInCommonCountSync(reviewRecipient.getRecipient().getId());
  }

  void block(@NonNull ReviewCard reviewCard, @NonNull Runnable onActionCompleteListener) {
    SignalExecutors.BOUNDED.execute(() -> {
      RecipientUtil.blockNonGroup(context, reviewCard.getReviewRecipient());
      onActionCompleteListener.run();
    });
  }

  void delete(@NonNull ReviewCard reviewCard, @NonNull Runnable onActionCompleteListener) {
    if (recipientId == null) {
      throw new UnsupportedOperationException();
    }

    SignalExecutors.BOUNDED.execute(() -> {
      Recipient resolved = Recipient.resolved(recipientId);

      if (resolved.isGroup()) throw new AssertionError();

      if (SignalStore.account().isMultiDevice()) {
        AppDependencies.getJobManager().add(MultiDeviceMessageRequestResponseJob.forDelete(recipientId));
      }

      ThreadTable threadTable = SignalDatabase.threads();
      long        threadId    = Objects.requireNonNull(threadTable.getThreadIdFor(recipientId));

      threadTable.deleteConversation(threadId, false);
      onActionCompleteListener.run();
    });
  }

  void removeFromGroup(@NonNull ReviewCard reviewCard, @NonNull OnRemoveFromGroupListener onRemoveFromGroupListener) {
    if (groupId == null) {
      throw new UnsupportedOperationException();
    }

    SignalExecutors.BOUNDED.execute(() -> {
      try {
        GroupManager.ejectAndBanFromGroup(context, groupId, reviewCard.getReviewRecipient());
        onRemoveFromGroupListener.onActionCompleted();
      } catch (GroupChangeException | IOException e) {
        onRemoveFromGroupListener.onActionFailed();
      }
    });
  }

  private static void loadRecipientsForGroup(@NonNull GroupId.V2 groupId,
                                             @NonNull OnRecipientsLoadedListener onRecipientsLoadedListener)
  {
    SignalExecutors.BOUNDED.execute(() -> {
      RecipientId groupRecipientId = SignalDatabase.recipients().getByGroupId(groupId).orElse(null);
      if (groupRecipientId != null) {
        onRecipientsLoadedListener.onRecipientsLoaded(SignalDatabase.nameCollisions().getCollisionsForThreadRecipientId(groupRecipientId));
      } else {
        onRecipientsLoadedListener.onRecipientsLoadFailed();
      }
    });
  }

  private static void loadSimilarRecipients(@NonNull RecipientId recipientId,
                                            @NonNull OnRecipientsLoadedListener onRecipientsLoadedListener)
  {
    SignalExecutors.BOUNDED.execute(() -> {
      onRecipientsLoadedListener.onRecipientsLoaded(SignalDatabase.nameCollisions().getCollisionsForThreadRecipientId(recipientId));
    });
  }

  interface OnRecipientsLoadedListener {
    void onRecipientsLoaded(@NonNull List<ReviewRecipient> recipients);
    void onRecipientsLoadFailed();
  }

  interface OnRemoveFromGroupListener {
    void onActionCompleted();
    void onActionFailed();
  }
}
