package dev.chat.fork.messenger.migrations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.signal.core.util.logging.Log;
import dev.chat.fork.messenger.dependencies.AppDependencies;
import dev.chat.fork.messenger.jobmanager.Job;
import dev.chat.fork.messenger.jobs.RefreshAttributesJob;
import dev.chat.fork.messenger.jobs.RefreshOwnProfileJob;

/**
 * Schedules a re-upload of the users attributes followed by a download of their profile.
 */
public final class AttributesMigrationJob extends MigrationJob {

  private static final String TAG = Log.tag(AttributesMigrationJob.class);

  public static final String KEY = "AttributesMigrationJob";

  AttributesMigrationJob() {
    this(new Parameters.Builder().build());
  }

  private AttributesMigrationJob(@NonNull Parameters parameters) {
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
    Log.i(TAG, "Scheduling attributes upload and profile refresh job chain");
    AppDependencies.getJobManager().startChain(new RefreshAttributesJob())
                   .then(new RefreshOwnProfileJob())
                   .enqueue();
  }

  @Override
  boolean shouldRetry(@NonNull Exception e) {
    return false;
  }

  public static class Factory implements Job.Factory<AttributesMigrationJob> {
    @Override
    public @NonNull AttributesMigrationJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      return new AttributesMigrationJob(parameters);
    }
  }
}
