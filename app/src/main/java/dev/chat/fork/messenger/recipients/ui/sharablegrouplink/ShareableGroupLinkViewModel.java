package dev.chat.fork.messenger.recipients.ui.sharablegrouplink;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import dev.chat.fork.messenger.groups.GroupId;
import dev.chat.fork.messenger.groups.LiveGroup;
import dev.chat.fork.messenger.groups.ui.GroupChangeFailureReason;
import dev.chat.fork.messenger.groups.ui.GroupErrors;
import dev.chat.fork.messenger.groups.v2.GroupLinkUrlAndStatus;
import dev.chat.fork.messenger.util.AsynchronousCallback;
import dev.chat.fork.messenger.util.SingleLiveEvent;
import dev.chat.fork.messenger.util.livedata.LiveDataUtil;

final class ShareableGroupLinkViewModel extends ViewModel {

  private final ShareableGroupLinkRepository    repository;
  private final LiveData<GroupLinkUrlAndStatus> groupLink;
  private final SingleLiveEvent<Integer>        toasts;
  private final SingleLiveEvent<Boolean>        busy;
  private final LiveData<Boolean>               canEdit;

  private ShareableGroupLinkViewModel(@NonNull GroupId.V2 groupId, @NonNull ShareableGroupLinkRepository repository) {
    LiveGroup liveGroup = new LiveGroup(groupId);

    this.repository = repository;
    this.groupLink  = liveGroup.getGroupLink();
    this.canEdit    = LiveDataUtil.combineLatest(liveGroup.isSelfAdmin(), liveGroup.isActive(), (admin, active) -> admin && active);
    this.toasts     = new SingleLiveEvent<>();
    this.busy       = new SingleLiveEvent<>();
  }

  LiveData<GroupLinkUrlAndStatus> getGroupLink() {
    return groupLink;
  }

  LiveData<Integer> getToasts() {
    return toasts;
  }

  LiveData<Boolean> getBusy() {
    return busy;
  }

  LiveData<Boolean> getCanEdit() {
    return canEdit;
  }

  void onToggleGroupLink() {
    busy.setValue(true);
    repository.toggleGroupLinkEnabled(new AsynchronousCallback.WorkerThread<Void, GroupChangeFailureReason>() {
      @Override
      public void onComplete(@Nullable Void result) {
        busy.postValue(false);
      }

      @Override
      public void onError(@Nullable GroupChangeFailureReason error) {
        busy.postValue(false);
        toasts.postValue(GroupErrors.getUserDisplayMessage(error));
      }
    });
  }

  void onToggleApproveMembers() {
    busy.setValue(true);
    repository.toggleGroupLinkApprovalRequired(new AsynchronousCallback.WorkerThread<Void, GroupChangeFailureReason>() {
      @Override
      public void onComplete(@Nullable Void result) {
        busy.postValue(false);
      }

      @Override
      public void onError(@Nullable GroupChangeFailureReason error) {
        busy.postValue(false);
        toasts.postValue(GroupErrors.getUserDisplayMessage(error));
      }
    });
  }

  void onResetLink() {
    busy.setValue(true);
    repository.cycleGroupLinkPassword(new AsynchronousCallback.WorkerThread<Void, GroupChangeFailureReason>() {
      @Override
      public void onComplete(@Nullable Void result) {
         busy.postValue(false);
      }

      @Override
      public void onError(@Nullable GroupChangeFailureReason error) {
        busy.postValue(false);
        toasts.postValue(GroupErrors.getUserDisplayMessage(error));
      }
    });
  }

  public static final class Factory implements ViewModelProvider.Factory {

    private final GroupId.V2                   groupId;
    private final ShareableGroupLinkRepository repository;

    public Factory(@NonNull GroupId.V2 groupId, @NonNull ShareableGroupLinkRepository repository) {
      this.groupId    = groupId;
      this.repository = repository;
    }

    @Override
    public @NonNull <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
      //noinspection ConstantConditions
      return modelClass.cast(new ShareableGroupLinkViewModel(groupId, repository));
    }
  }
}
