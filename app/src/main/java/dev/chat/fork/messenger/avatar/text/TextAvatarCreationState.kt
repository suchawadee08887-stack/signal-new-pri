package dev.chat.fork.messenger.avatar.text

import dev.chat.fork.messenger.avatar.Avatar
import dev.chat.fork.messenger.avatar.AvatarColorItem
import dev.chat.fork.messenger.avatar.Avatars

data class TextAvatarCreationState(
  val currentAvatar: Avatar.Text
) {
  fun colors(): List<AvatarColorItem> = Avatars.colors.map { AvatarColorItem(it, currentAvatar.color == it) }
}
