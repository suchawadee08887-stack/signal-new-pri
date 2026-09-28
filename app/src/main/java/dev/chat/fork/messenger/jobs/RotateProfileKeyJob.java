package dev.chat.fork.messenger.jobs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.signal.libsignal.zkgroup.profiles.ProfileKey;
import dev.chat.fork.messenger.crypto.ProfileKeyUtil;
import dev.chat.fork.messenger.database.SignalDatabase;
import dev.chat.fork.messenger.jobmanager.JsonJobData;
import dev.chat.fork.messenger.jobmanager.Job;
import dev.chat.fork.messenger.recipients.Recipient;

public class RotateProfileKeyJob extends BaseJob {

  public static String KEY = "RotateProfileKeyJob";

  public RotateProfileKeyJob() {
    this(new Job.Parameters.Builder()
                           .setQueue("__ROTATE_PROFILE_KEY__")
                           .setMaxInstancesForFactory(2)
                           .build());
  }

  private RotateProfileKeyJob(@NonNull Job.Parameters parameters) {
    super(parameters);
  }

  @Override
  public @Nullable byte[] serialize() {
    return null;
  }

  @Override
  public @NonNull String getFactoryKey() {
    return KEY;
  }

  @Override
  public void onRun() {
    ProfileKey newProfileKey = ProfileKeyUtil.createNew();
    Recipient  self          = Recipient.self();

    SignalDatabase.recipients().setProfileKey(self.getId(), newProfileKey);
  }

  @Override
  public void onFailure() {
  }

  @Override
  protected boolean onShouldRetry(@NonNull Exception exception) {
    return false;
  }

  public static final class Factory implements Job.Factory<RotateProfileKeyJob> {
    @Override
    public @NonNull RotateProfileKeyJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      return new RotateProfileKeyJob(parameters);
    }
  }
}
