package dev.chat.fork.messenger.logsubmit;

import android.content.Context;

import androidx.annotation.NonNull;

import dev.chat.fork.messenger.AppCapabilities;
import dev.chat.fork.messenger.database.SignalDatabase;
import dev.chat.fork.messenger.database.model.RecipientRecord;
import dev.chat.fork.messenger.keyvalue.SignalStore;
import dev.chat.fork.messenger.recipients.Recipient;
import dev.chat.fork.messenger.util.RemoteConfig;
import org.whispersystems.signalservice.api.account.AccountAttributes;

public final class LogSectionCapabilities implements LogSection {

  @Override
  public @NonNull String getTitle() {
    return "CAPABILITIES";
  }

  @Override
  public @NonNull CharSequence getContent(@NonNull Context context) {
    if (!SignalStore.account().isRegistered()) {
      return "Unregistered";
    }

    if (SignalStore.account().getAci() == null) {
      return "Self not yet available!";
    }

    Recipient self = Recipient.self();

    AccountAttributes.Capabilities localCapabilities  = AppCapabilities.getCapabilities(false);
    RecipientRecord.Capabilities   globalCapabilities = SignalDatabase.recipients().getCapabilities(self.getId());

    StringBuilder builder = new StringBuilder().append("-- Local").append("\n")
                                               .append("VersionedExpirationTimer: ").append(localCapabilities.getVersionedExpirationTimer()).append("\n")
                                               .append("\n")
                                               .append("-- Global").append("\n")
                                               .append("None").append("\n");

    // Left as an example for when we want to add new ones
//    if (globalCapabilities != null) {
//      builder.append("StorageServiceEncryptionV2: ").append(globalCapabilities.getStorageServiceEncryptionV2()).append("\n");
//      builder.append("\n");
//    } else {
//      builder.append("Self not found!");
//    }

    return builder;
  }
}
