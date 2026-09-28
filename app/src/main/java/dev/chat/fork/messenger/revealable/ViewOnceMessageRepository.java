package dev.chat.fork.messenger.revealable;

import android.content.Context;

import androidx.annotation.NonNull;

import org.signal.core.util.concurrent.SignalExecutors;
import org.signal.core.util.logging.Log;
import dev.chat.fork.messenger.database.MessageTable;
import dev.chat.fork.messenger.database.NoSuchMessageException;
import dev.chat.fork.messenger.database.SignalDatabase;
import dev.chat.fork.messenger.database.model.MmsMessageRecord;
import dev.chat.fork.messenger.dependencies.AppDependencies;
import dev.chat.fork.messenger.jobs.MultiDeviceViewedUpdateJob;
import dev.chat.fork.messenger.jobs.SendViewedReceiptJob;

import java.util.Collections;
import java.util.Optional;

class ViewOnceMessageRepository {

  private static final String TAG = Log.tag(ViewOnceMessageRepository.class);

  private final MessageTable mmsDatabase;

  ViewOnceMessageRepository(@NonNull Context context) {
    this.mmsDatabase = SignalDatabase.messages();
  }

  void getMessage(long messageId, @NonNull Callback<Optional<MmsMessageRecord>> callback) {
    SignalExecutors.BOUNDED.execute(() -> {
      try {
        MmsMessageRecord record = (MmsMessageRecord) mmsDatabase.getMessageRecord(messageId);

        MessageTable.MarkedMessageInfo info = mmsDatabase.setIncomingMessageViewed(record.getId());
        if (info != null) {
          AppDependencies.getJobManager().add(new SendViewedReceiptJob(record.getThreadId(),
                                                                       info.getSyncMessageId().getRecipientId(),
                                                                       info.getSyncMessageId().getTimetamp(),
                                                                       info.getMessageId()));
          MultiDeviceViewedUpdateJob.enqueue(Collections.singletonList(info.getSyncMessageId()));
        }

        callback.onComplete(Optional.ofNullable(record));
      } catch (NoSuchMessageException e) {
        callback.onComplete(Optional.empty());
      }
    });
  }

  interface Callback<T> {
    void onComplete(T result);
  }
}
