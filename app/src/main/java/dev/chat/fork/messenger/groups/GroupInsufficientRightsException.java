package dev.chat.fork.messenger.groups;

public final class GroupInsufficientRightsException extends GroupChangeException {

  public GroupInsufficientRightsException(Throwable throwable) {
    super(throwable);
  }
}
