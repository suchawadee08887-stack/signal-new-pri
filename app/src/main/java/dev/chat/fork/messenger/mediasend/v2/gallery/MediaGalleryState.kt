package dev.chat.fork.messenger.mediasend.v2.gallery

import dev.chat.fork.messenger.util.adapter.mapping.MappingModel

data class MediaGalleryState(
  val bucketId: String?,
  val bucketTitle: String?,
  val items: List<MappingModel<*>> = listOf()
)
