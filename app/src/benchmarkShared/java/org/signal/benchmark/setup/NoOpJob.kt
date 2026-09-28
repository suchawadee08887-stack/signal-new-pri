package org.signal.benchmark.setup

import dev.chat.fork.messenger.jobmanager.Job
import dev.chat.fork.messenger.jobs.AccountConsistencyWorkerJob
import dev.chat.fork.messenger.jobs.ArchiveBackupIdReservationJob
import dev.chat.fork.messenger.jobs.AvatarGroupsV2DownloadJob
import dev.chat.fork.messenger.jobs.CreateReleaseChannelJob
import dev.chat.fork.messenger.jobs.DirectoryRefreshJob
import dev.chat.fork.messenger.jobs.DownloadLatestEmojiDataJob
import dev.chat.fork.messenger.jobs.EmojiSearchIndexDownloadJob
import dev.chat.fork.messenger.jobs.FontDownloaderJob
import dev.chat.fork.messenger.jobs.GroupRingCleanupJob
import dev.chat.fork.messenger.jobs.GroupV2UpdateSelfProfileKeyJob
import dev.chat.fork.messenger.jobs.LinkedDeviceInactiveCheckJob
import dev.chat.fork.messenger.jobs.MultiDeviceProfileKeyUpdateJob
import dev.chat.fork.messenger.jobs.PostRegistrationBackupRedemptionJob
import dev.chat.fork.messenger.jobs.PreKeysSyncJob
import dev.chat.fork.messenger.jobs.ProfileUploadJob
import dev.chat.fork.messenger.jobs.RefreshAttributesJob
import dev.chat.fork.messenger.jobs.RefreshSvrCredentialsJob
import dev.chat.fork.messenger.jobs.RequestGroupV2InfoJob
import dev.chat.fork.messenger.jobs.ResetSvrGuessCountJob
import dev.chat.fork.messenger.jobs.RestoreOptimizedMediaJob
import dev.chat.fork.messenger.jobs.RetrieveProfileAvatarJob
import dev.chat.fork.messenger.jobs.RetrieveProfileJob
import dev.chat.fork.messenger.jobs.RetrieveRemoteAnnouncementsJob
import dev.chat.fork.messenger.jobs.RotateCertificateJob
import dev.chat.fork.messenger.jobs.StickerPackDownloadJob
import dev.chat.fork.messenger.jobs.StorageSyncJob
import dev.chat.fork.messenger.jobs.StoryOnboardingDownloadJob

/**
 * A [Job] that does nothing and always succeeds. Test setups substitute this for jobs whose
 * real implementations would hit the network at startup (and so would either generate noise
 * against the [DeviceTransferBlockingInterceptor][dev.chat.fork.messenger.net.DeviceTransferBlockingInterceptor]
 * or fail against unstubbed mocks). Use [replaceFactories] to apply the swap.
 */
class NoOpJob(parameters: Parameters) : Job(parameters) {
  override fun serialize(): ByteArray? = null
  override fun getFactoryKey(): String = KEY
  override fun run(): Result = Result.success()
  override fun onFailure() = Unit

  class Factory : Job.Factory<NoOpJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): NoOpJob = NoOpJob(parameters)
  }

  companion object {
    const val KEY = "NoOpJob"

    private val STARTUP_NETWORK_JOB_KEYS: Set<String> = setOf(
      AccountConsistencyWorkerJob.KEY,
      ArchiveBackupIdReservationJob.KEY,
      AvatarGroupsV2DownloadJob.KEY,
      CreateReleaseChannelJob.KEY,
      DirectoryRefreshJob.KEY,
      DownloadLatestEmojiDataJob.KEY,
      EmojiSearchIndexDownloadJob.KEY,
      FontDownloaderJob.KEY,
      GroupRingCleanupJob.KEY,
      GroupV2UpdateSelfProfileKeyJob.KEY,
      LinkedDeviceInactiveCheckJob.KEY,
      MultiDeviceProfileKeyUpdateJob.KEY,
      PostRegistrationBackupRedemptionJob.KEY,
      PreKeysSyncJob.KEY,
      ProfileUploadJob.KEY,
      RefreshAttributesJob.KEY,
      RefreshSvrCredentialsJob.KEY,
      RequestGroupV2InfoJob.KEY,
      ResetSvrGuessCountJob.KEY,
      RestoreOptimizedMediaJob.KEY,
      RetrieveProfileAvatarJob.KEY,
      RetrieveProfileJob.KEY,
      RetrieveRemoteAnnouncementsJob.KEY,
      RotateCertificateJob.KEY,
      StickerPackDownloadJob.KEY,
      StorageSyncJob.KEY,
      StoryOnboardingDownloadJob.KEY
    )

    fun replaceFactories(factories: Map<String, Job.Factory<*>>): Map<String, Job.Factory<*>> = factories.mapValues { if (it.key in STARTUP_NETWORK_JOB_KEYS) Factory() else it.value }
  }
}
