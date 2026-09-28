package dev.chat.fork.messenger.deeplinks;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import dev.chat.fork.messenger.MainActivity;
import dev.chat.fork.messenger.PassphraseRequiredActivity;

public class DeepLinkEntryActivity extends PassphraseRequiredActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState, boolean ready) {
    Intent intent = MainActivity.clearTop(this);
    Uri    data   = getIntent().getData();
    intent.setData(data);
    startActivity(intent);
  }
}
