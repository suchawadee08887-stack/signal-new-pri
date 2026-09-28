package dev.chat.fork.messenger.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import dev.chat.fork.messenger.dependencies.AppDependencies;
import dev.chat.fork.messenger.jobs.MessageFetchJob;

public class BootReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    AppDependencies.getJobManager().add(new MessageFetchJob());
  }
}
