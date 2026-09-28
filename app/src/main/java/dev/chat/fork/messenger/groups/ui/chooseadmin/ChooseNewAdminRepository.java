package dev.chat.fork.messenger.groups.ui.chooseadmin;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import dev.chat.fork.messenger.groups.GroupChangeException;
import dev.chat.fork.messenger.groups.GroupId;
import dev.chat.fork.messenger.groups.GroupManager;
import dev.chat.fork.messenger.groups.ui.GroupChangeFailureReason;
import dev.chat.fork.messenger.groups.ui.GroupChangeResult;
import dev.chat.fork.messenger.recipients.RecipientId;

import java.io.IOException;
import java.util.List;

public final class ChooseNewAdminRepository {
  private final Application context;

  ChooseNewAdminRepository(@NonNull Application context) {
    this.context = context;
  }

  @WorkerThread
  @NonNull GroupChangeResult updateAdminsAndLeave(@NonNull GroupId.V2 groupId, @NonNull List<RecipientId> newAdminIds) {
    try {
      GroupManager.addMemberAdminsAndLeaveGroup(context, groupId, newAdminIds);
      return GroupChangeResult.SUCCESS;
    } catch (GroupChangeException | IOException e) {
      return GroupChangeResult.failure(GroupChangeFailureReason.fromException(e));
    }
  }
}
