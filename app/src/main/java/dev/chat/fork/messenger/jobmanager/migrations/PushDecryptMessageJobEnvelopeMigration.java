package dev.chat.fork.messenger.jobmanager.migrations;

import androidx.annotation.NonNull;

import dev.chat.fork.messenger.jobmanager.JobMigration;

/**
 * We removed the messageId property from the job data and replaced it with a serialized envelope,
 * so we need to take jobs that referenced an ID and replace it with the envelope instead.
 *
 * @deprecated No longer have a PushDecryptJob to migrate, job now maps to {@link dev.chat.fork.messenger.jobs.FailingJob}
 * in {@link dev.chat.fork.messenger.jobs.JobManagerFactories}
 */
public class PushDecryptMessageJobEnvelopeMigration extends JobMigration {

  public PushDecryptMessageJobEnvelopeMigration() {
    super(8);
  }

  @Override
  public @NonNull JobData migrate(@NonNull JobData jobData) {
    return jobData;
  }
}
