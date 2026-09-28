package dev.chat.fork.messenger.migrations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dev.chat.fork.messenger.jobmanager.Job;
import dev.chat.fork.messenger.keyvalue.SignalStore;

import java.util.concurrent.TimeUnit;

public class PinReminderMigrationJob extends MigrationJob {

  public static final String KEY = "PinReminderMigrationJob";

  PinReminderMigrationJob() {
    this(new Job.Parameters.Builder().build());
  }

  private PinReminderMigrationJob(@NonNull Parameters parameters) {
    super(parameters);
  }

  @Override
  boolean isUiBlocking() {
    return false;
  }

  @Override
  public @NonNull String getFactoryKey() {
    return KEY;
  }

  @Override
  void performMigration() {
    SignalStore.pin().setNextReminderIntervalToAtMost(TimeUnit.DAYS.toMillis(3));
  }

  @Override
  boolean shouldRetry(@NonNull Exception e) {
    return false;
  }

  public static class Factory implements Job.Factory<PinReminderMigrationJob> {

    @Override
    public @NonNull PinReminderMigrationJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      return new PinReminderMigrationJob(parameters);
    }
  }
}
