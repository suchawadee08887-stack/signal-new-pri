/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.banner.banners

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.signal.core.util.bytes
import dev.chat.fork.messenger.backup.ArchiveUploadProgress
import dev.chat.fork.messenger.backup.v2.ui.status.ArchiveUploadStatusBannerView
import dev.chat.fork.messenger.backup.v2.ui.status.ArchiveUploadStatusBannerViewEvents
import dev.chat.fork.messenger.backup.v2.ui.status.ArchiveUploadStatusBannerViewState
import dev.chat.fork.messenger.banner.Banner
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.jobmanager.impl.NetworkConstraint
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.keyvalue.protos.ArchiveUploadProgressState
import dev.chat.fork.messenger.util.NetworkUtil

@OptIn(ExperimentalCoroutinesApi::class)
class ArchiveUploadStatusBanner(private val listener: UploadProgressBannerListener) : Banner<ArchiveUploadStatusBannerViewState>() {

  override val enabled: Boolean
    get() = SignalStore.backup.uploadBannerVisible

  override val dataFlow: Flow<ArchiveUploadStatusBannerViewState> by lazy {
    ArchiveUploadProgress
      .progress
      .map {
        val hasMobileData = NetworkUtil.isConnectedMobile(AppDependencies.application)
        val hasWifi = NetworkUtil.isConnectedWifi(AppDependencies.application)
        val canUploadOnCellular = SignalStore.backup.backupWithCellular

        if (hasWifi || (hasMobileData && canUploadOnCellular)) {
          when (it.state) {
            ArchiveUploadProgressState.State.None -> ArchiveUploadStatusBannerViewState.Finished(it.completedSize.bytes.toUnitString(maxPlaces = 1))
            ArchiveUploadProgressState.State.Export -> ArchiveUploadStatusBannerViewState.CreatingBackupFile
            ArchiveUploadProgressState.State.UploadBackupFile,
            ArchiveUploadProgressState.State.UploadMedia -> {
              if (NetworkConstraint.isMet(AppDependencies.application)) {
                ArchiveUploadStatusBannerViewState.Uploading(
                  completedSize = it.completedSize.bytes.toUnitString(maxPlaces = 1),
                  totalSize = it.totalSize.bytes.toUnitString(maxPlaces = 1),
                  progress = it.completedSize / it.totalSize.toFloat()
                )
              } else {
                ArchiveUploadStatusBannerViewState.PausedNoInternet
              }
            }
            ArchiveUploadProgressState.State.UserCanceled -> ArchiveUploadStatusBannerViewState.CreatingBackupFile
          }
        } else if (hasMobileData) {
          when (it.state) {
            ArchiveUploadProgressState.State.None,
            ArchiveUploadProgressState.State.Export,
            ArchiveUploadProgressState.State.UploadBackupFile,
            ArchiveUploadProgressState.State.UploadMedia -> {
              ArchiveUploadStatusBannerViewState.PausedMissingWifi
            }
            ArchiveUploadProgressState.State.UserCanceled -> ArchiveUploadStatusBannerViewState.CreatingBackupFile
          }
        } else {
          when (it.state) {
            ArchiveUploadProgressState.State.None,
            ArchiveUploadProgressState.State.Export,
            ArchiveUploadProgressState.State.UploadBackupFile,
            ArchiveUploadProgressState.State.UploadMedia -> {
              ArchiveUploadStatusBannerViewState.PausedNoInternet
            }
            ArchiveUploadProgressState.State.UserCanceled -> ArchiveUploadStatusBannerViewState.CreatingBackupFile
          }
        }
      }
  }

  override val stateUpdates: Flow<Unit>
    get() = ArchiveUploadProgress.progress
      .map { enabled }
      .distinctUntilChanged()
      .map { }

  @Composable
  override fun DisplayBanner(model: ArchiveUploadStatusBannerViewState, contentPadding: PaddingValues) {
    ArchiveUploadStatusBannerView(
      state = model,
      emitter = { event ->
        when (event) {
          ArchiveUploadStatusBannerViewEvents.BannerClicked -> {
            listener.onBannerClick()
          }
          ArchiveUploadStatusBannerViewEvents.CancelClicked -> {
            listener.onCancelClicked()
          }
          ArchiveUploadStatusBannerViewEvents.HideClicked -> {
            SignalStore.backup.uploadBannerVisible = false
            ArchiveUploadProgress.triggerUpdate()
          }
        }
      }
    )
  }

  private val ArchiveUploadProgressState.completedSize get() = this.mediaUploadedBytes + this.backupFileUploadedBytes
  private val ArchiveUploadProgressState.totalSize get() = this.mediaTotalBytes + this.backupFileTotalBytes

  interface UploadProgressBannerListener {
    fun onBannerClick()
    fun onCancelClicked()
  }
}
