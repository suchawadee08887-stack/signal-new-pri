package dev.chat.fork.messenger.payments.preferences.model;

import androidx.annotation.NonNull;

import dev.chat.fork.messenger.util.adapter.mapping.MappingModel;

public class NoRecentActivity implements MappingModel<NoRecentActivity> {
  @Override
  public boolean areItemsTheSame(@NonNull NoRecentActivity newItem) {
    return true;
  }

  @Override
  public boolean areContentsTheSame(@NonNull NoRecentActivity newItem) {
    return true;
  }
}
