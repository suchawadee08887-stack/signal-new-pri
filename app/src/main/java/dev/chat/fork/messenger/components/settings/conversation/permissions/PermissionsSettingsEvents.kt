package dev.chat.fork.messenger.components.settings.conversation.permissions

import dev.chat.fork.messenger.groups.ui.GroupChangeFailureReason

sealed class PermissionsSettingsEvents {
  class GroupChangeError(val reason: GroupChangeFailureReason) : PermissionsSettingsEvents()
  object ShowMemberLabelsWillBeRemovedWarning : PermissionsSettingsEvents()
}
