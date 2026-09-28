package dev.chat.fork.messenger.components.webrtc;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.core.util.Consumer;

import org.signal.core.util.concurrent.SignalExecutors;
import dev.chat.fork.messenger.database.GroupTable;
import dev.chat.fork.messenger.database.SignalDatabase;
import dev.chat.fork.messenger.database.identity.IdentityRecordList;
import dev.chat.fork.messenger.dependencies.AppDependencies;
import dev.chat.fork.messenger.recipients.Recipient;

import java.util.Collections;
import java.util.List;

public final class WebRtcCallRepository {

  private WebRtcCallRepository() {}

  @WorkerThread
  public static void getIdentityRecords(@NonNull Recipient recipient, @NonNull Consumer<IdentityRecordList> consumer) {
    SignalExecutors.BOUNDED.execute(() -> {
      List<Recipient> recipients;

      if (recipient.isGroup()) {
        recipients = SignalDatabase.groups().getGroupMembers(recipient.requireGroupId(), GroupTable.MemberSet.FULL_MEMBERS_EXCLUDING_SELF);
      } else {
        recipients = Collections.singletonList(recipient);
      }

      consumer.accept(AppDependencies.getProtocolStore().aci().identities().getIdentityRecords(recipients));
    });
  }
}
