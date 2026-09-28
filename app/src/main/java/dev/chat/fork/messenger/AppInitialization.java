package dev.chat.fork.messenger;

import android.content.Context;

import androidx.annotation.NonNull;

import org.signal.core.util.logging.Log;
import dev.chat.fork.messenger.dependencies.AppDependencies;
import dev.chat.fork.messenger.jobmanager.JobManager;
import dev.chat.fork.messenger.jobs.DeleteAbandonedAttachmentsJob;
import dev.chat.fork.messenger.jobs.EmojiSearchIndexDownloadJob;
import dev.chat.fork.messenger.jobs.QuoteThumbnailBackfillJob;
import dev.chat.fork.messenger.jobs.StickerPackDownloadJob;
import dev.chat.fork.messenger.keyvalue.SignalStore;
import dev.chat.fork.messenger.migrations.ApplicationMigrations;
import dev.chat.fork.messenger.migrations.QuoteThumbnailBackfillMigrationJob;
import dev.chat.fork.messenger.stickers.BlessedPacks;
import dev.chat.fork.messenger.util.TextSecurePreferences;
import org.signal.core.util.Util;

/**
 * Rule of thumb: if there's something you want to do on the first app launch that involves
 * persisting state to the database, you'll almost certainly *also* want to do it post backup
 * restore, since a backup restore will wipe the current state of the database.
 */
public final class AppInitialization {

  private static final String TAG = Log.tag(AppInitialization.class);

  private AppInitialization() {}

  public static void onFirstEverAppLaunch(@NonNull Context context) {
    Log.i(TAG, "onFirstEverAppLaunch()");

    TextSecurePreferences.setAppMigrationVersion(context, ApplicationMigrations.CURRENT_VERSION);
    TextSecurePreferences.setJobManagerVersion(context, JobManager.CURRENT_VERSION);
    TextSecurePreferences.setLastVersionCode(context, BuildConfig.VERSION_CODE);
    TextSecurePreferences.setHasSeenStickerIntroTooltip(context, true);
    SignalStore.settings().setPassphraseDisabled(true);
    TextSecurePreferences.setReadReceiptsEnabled(context, true);
    TextSecurePreferences.setTypingIndicatorsEnabled(context, true);
    AppDependencies.getMegaphoneRepository().onFirstEverAppLaunch();
    SignalStore.onFirstEverAppLaunch();
    AppDependencies.getJobManager().addAll(BlessedPacks.getFirstInstallJobs());
  }

  public static void onPostBackupRestore(@NonNull Context context) {
    Log.i(TAG, "onPostBackupRestore()");

    AppDependencies.getMegaphoneRepository().onFirstEverAppLaunch();
    SignalStore.onPostBackupRestore();
    SignalStore.onFirstEverAppLaunch();
    SignalStore.onboarding().clearAll();
    SignalStore.settings().setPassphraseDisabled(true);
    SignalStore.notificationProfile().setHasSeenTooltip(true);
    TextSecurePreferences.onPostBackupRestore(context);
    SignalStore.settings().setPassphraseDisabled(true);
    AppDependencies.getJobManager().addAll(BlessedPacks.getFirstInstallJobs());
    EmojiSearchIndexDownloadJob.scheduleImmediately();
    DeleteAbandonedAttachmentsJob.enqueue();

    if (SignalStore.misc().startedQuoteThumbnailMigration()) {
      AppDependencies.getJobManager().add(new QuoteThumbnailBackfillJob());
    } else {
      AppDependencies.getJobManager().add(new QuoteThumbnailBackfillMigrationJob());
    }
  }

  /**
   * Temporary migration method that does the safest bits of {@link #onFirstEverAppLaunch(Context)}
   */
  public static void onRepairFirstEverAppLaunch(@NonNull Context context) {
    Log.w(TAG, "onRepairFirstEverAppLaunch()");

    TextSecurePreferences.setAppMigrationVersion(context, ApplicationMigrations.CURRENT_VERSION);
    TextSecurePreferences.setJobManagerVersion(context, JobManager.CURRENT_VERSION);
    TextSecurePreferences.setLastVersionCode(context, BuildConfig.VERSION_CODE);
    TextSecurePreferences.setHasSeenStickerIntroTooltip(context, true);
    SignalStore.settings().setPassphraseDisabled(true);
    AppDependencies.getMegaphoneRepository().onFirstEverAppLaunch();
    SignalStore.onFirstEverAppLaunch();
    AppDependencies.getJobManager().addAll(BlessedPacks.getFirstInstallJobs());
  }
}
