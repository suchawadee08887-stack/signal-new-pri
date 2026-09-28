package dev.chat.fork.messenger.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import dev.chat.fork.messenger.jobs.EmojiSearchIndexDownloadJob;

public class LocaleChangedReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    NotificationChannels.getInstance().onLocaleChanged();
    EmojiSearchIndexDownloadJob.scheduleImmediately();
  }
}
