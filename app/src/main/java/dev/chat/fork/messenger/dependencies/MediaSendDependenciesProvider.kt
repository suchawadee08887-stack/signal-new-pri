/*
 * Copyright 2026 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.dependencies

import androidx.media3.exoplayer.ExoPlayer
import org.signal.core.util.contentproviders.BlobProvider
import org.signal.mediasend.MediaSendDependencies
import org.signal.mediasend.MediaSendQrRepository
import org.signal.mediasend.MediaSendRepository
import org.signal.mediasend.preupload.PreUploadRepository
import org.signal.video.exo.ExoPlayerPool
import dev.chat.fork.messenger.media.DecryptableUriMediaInput
import dev.chat.fork.messenger.mediasend.v3.MediaSendV3PreUploadRepository
import dev.chat.fork.messenger.mediasend.v3.MediaSendV3QrRepository
import dev.chat.fork.messenger.mediasend.v3.MediaSendV3Repository
import dev.chat.fork.messenger.video.interfaces.MediaInputFactory

object MediaSendDependenciesProvider : MediaSendDependencies.Provider {
  override fun provideMediaSendRepository(): MediaSendRepository = MediaSendV3Repository

  override fun providePreUploadRepository(): PreUploadRepository = MediaSendV3PreUploadRepository

  override fun provideQrRepository(): MediaSendQrRepository = MediaSendV3QrRepository

  override fun provideExoPlayerPool(): ExoPlayerPool<ExoPlayer> = AppDependencies.exoPlayerPool

  override fun provideBlobs(): BlobProvider = AppDependencies.blobs

  override fun provideMediaInputFactory(): MediaInputFactory = DecryptableUriMediaInput
}
