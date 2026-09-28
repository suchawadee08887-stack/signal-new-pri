package dev.chat.fork.messenger.avatar.vector

import dev.chat.fork.messenger.avatar.Avatar
import dev.chat.fork.messenger.avatar.AvatarColorItem
import dev.chat.fork.messenger.avatar.Avatars

data class VectorAvatarCreationState(
  val currentAvatar: Avatar.Vector
) {
  fun colors(): List<AvatarColorItem> = Avatars.colors.map { AvatarColorItem(it, currentAvatar.color == it) }
}
