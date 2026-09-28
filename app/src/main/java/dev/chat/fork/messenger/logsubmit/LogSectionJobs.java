package dev.chat.fork.messenger.logsubmit;

import android.content.Context;

import androidx.annotation.NonNull;

import dev.chat.fork.messenger.dependencies.AppDependencies;

public class LogSectionJobs implements LogSection {

  @Override
  public @NonNull String getTitle() {
    return "JOBS";
  }

  @Override
  public @NonNull CharSequence getContent(@NonNull Context context) {
    return AppDependencies.getJobManager().getDebugInfo();
  }
}
