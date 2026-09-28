package dev.chat.fork.messenger.migrations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dev.chat.fork.messenger.dependencies.AppDependencies;
import dev.chat.fork.messenger.jobmanager.Job;
import dev.chat.fork.messenger.jobs.StickerPackDownloadJob;
import dev.chat.fork.messenger.stickers.BlessedPacks;

/**
 * Installs Day by Day blessed pack.
 */
public class StickerDayByDayMigrationJob extends MigrationJob {

  public static final String KEY = "StickerDayByDayMigrationJob";

  StickerDayByDayMigrationJob() {
    this(new Parameters.Builder().build());
  }

  private StickerDayByDayMigrationJob(@NonNull Parameters parameters) {
    super(parameters);
  }

  @Override
  public boolean isUiBlocking() {
    return false;
  }

  @Override
  public @NonNull String getFactoryKey() {
    return KEY;
  }

  @Override
  public void performMigration() {
    AppDependencies.getJobManager().add(StickerPackDownloadJob.forInstall(BlessedPacks.DAY_BY_DAY.getPackId(), BlessedPacks.DAY_BY_DAY.getPackKey(), false));
  }

  @Override
  boolean shouldRetry(@NonNull Exception e) {
    return false;
  }

  public static class Factory implements Job.Factory<StickerDayByDayMigrationJob> {
    @Override
    public @NonNull StickerDayByDayMigrationJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      return new StickerDayByDayMigrationJob(parameters);
    }
  }
}
