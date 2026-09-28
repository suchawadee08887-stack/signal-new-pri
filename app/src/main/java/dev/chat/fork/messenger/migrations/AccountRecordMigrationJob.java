package dev.chat.fork.messenger.migrations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.signal.core.util.logging.Log;
import dev.chat.fork.messenger.database.SignalDatabase;
import dev.chat.fork.messenger.dependencies.AppDependencies;
import dev.chat.fork.messenger.jobmanager.Job;
import dev.chat.fork.messenger.jobs.StorageSyncJob;
import dev.chat.fork.messenger.keyvalue.SignalStore;
import dev.chat.fork.messenger.recipients.Recipient;

/**
 * Marks the AccountRecord as dirty and runs a storage sync. Can be enqueued when we've added a new
 * attribute to the AccountRecord.
 */
public class AccountRecordMigrationJob extends MigrationJob {

  private static final String TAG = Log.tag(AccountRecordMigrationJob.class);

  public static final String KEY = "AccountRecordMigrationJob";

  AccountRecordMigrationJob() {
    this(new Parameters.Builder().build());
  }

  private AccountRecordMigrationJob(@NonNull Parameters parameters) {
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
    if (!SignalStore.account().isRegistered() || SignalStore.account().getAci() == null) {
      Log.w(TAG, "Not registered!");
      return;
    }

    SignalDatabase.recipients().markNeedsSync(Recipient.self().getId());
    AppDependencies.getJobManager().add(StorageSyncJob.forLocalChange());
  }

  @Override
  boolean shouldRetry(@NonNull Exception e) {
    return false;
  }

  public static class Factory implements Job.Factory<AccountRecordMigrationJob> {
    @Override
    public @NonNull AccountRecordMigrationJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      return new AccountRecordMigrationJob(parameters);
    }
  }
}
