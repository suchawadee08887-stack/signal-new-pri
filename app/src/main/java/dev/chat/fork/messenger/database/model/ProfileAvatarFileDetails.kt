package dev.chat.fork.messenger.database.model

/**
 * Details related to the current avatar profile image file that would be returned via [dev.chat.fork.messenger.profiles.AvatarHelper.getAvatarFile]
 * at the time this [dev.chat.fork.messenger.recipients.Recipient] was loaded/refreshed from the database.
 */
data class ProfileAvatarFileDetails(
  val hashId: Long,
  val lastModified: Long
) {
  fun getDiskCacheKeyBytes(): ByteArray {
    return toString().toByteArray()
  }

  fun hasFile(): Boolean {
    return this != NO_DETAILS
  }

  companion object {
    @JvmField
    val NO_DETAILS = ProfileAvatarFileDetails(0, 0)
  }
}
