package dev.chat.fork.messenger.migrations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.signal.core.util.logging.Log;
import dev.chat.fork.messenger.dependencies.AppDependencies;
import dev.chat.fork.messenger.jobmanager.Job;
import dev.chat.fork.messenger.jobs.RefreshOwnProfileJob;

/**
 * We just want to make sure that the user has a profile avatar set in the RecipientDatabase, so
 * we're refreshing their own profile.
 */
public class AvatarIdRemovalMigrationJob extends MigrationJob {

  private static final String TAG = Log.tag(AvatarIdRemovalMigrationJob.class);

  public static final String KEY = "AvatarIdRemovalMigrationJob";

  AvatarIdRemovalMigrationJob() {
    this(new Parameters.Builder().build());
  }

  private AvatarIdRemovalMigrationJob(@NonNull Parameters parameters) {
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
    AppDependencies.getJobManager().add(new RefreshOwnProfileJob());
  }

  @Override
  boolean shouldRetry(@NonNull Exception e) {
    return false;
  }

  public static class Factory implements Job.Factory<AvatarIdRemovalMigrationJob> {
    @Override
    public @NonNull AvatarIdRemovalMigrationJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      return new AvatarIdRemovalMigrationJob(parameters);
    }
  }
}
