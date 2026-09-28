package dev.chat.fork.messenger.megaphone;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.signal.core.util.ThreadUtil;
import dev.chat.fork.messenger.PassphraseRequiredActivity;
import dev.chat.fork.messenger.R;
import dev.chat.fork.messenger.dependencies.AppDependencies;
import dev.chat.fork.messenger.util.DynamicNoActionBarTheme;
import dev.chat.fork.messenger.util.DynamicTheme;
import dev.chat.fork.messenger.util.PlayStoreUtil;
import dev.chat.fork.messenger.util.SystemWindowInsetsSetter;

/**
 * Shown when a users build fully expires. Controlled by {@link Megaphones.Event#CLIENT_DEPRECATED}.
 */
public class ClientDeprecatedActivity extends PassphraseRequiredActivity {

  private final DynamicTheme theme = new DynamicNoActionBarTheme();

  @Override
  protected void onCreate(Bundle savedInstanceState, boolean ready) {
    setContentView(R.layout.client_deprecated_activity);

    SystemWindowInsetsSetter.attach(findViewById(R.id.client_deprecated_root), this, WindowInsetsCompat.Type.systemBars());

    findViewById(R.id.client_deprecated_update_button).setOnClickListener(v -> onUpdateClicked());
    findViewById(R.id.client_deprecated_dont_update_button).setOnClickListener(v -> onDontUpdateClicked());

    getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
      @Override
      public void handleOnBackPressed() {
        // The user must explicitly choose to update or to dismiss this screen.
      }
    });
  }

  @Override
  protected void onPreCreate() {
    theme.onCreate(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    theme.onResume(this);
  }

  private void onUpdateClicked() {
    PlayStoreUtil.openPlayStoreOrOurApkDownloadPage(this);
  }

  private void onDontUpdateClicked() {
    new MaterialAlertDialogBuilder(this)
                   .setTitle(R.string.ClientDeprecatedActivity_warning)
                   .setMessage(R.string.ClientDeprecatedActivity_your_version_of_signal_has_expired_you_can_view_your_message_history)
                   .setPositiveButton(R.string.ClientDeprecatedActivity_dont_update, (dialog, which) -> {
                     AppDependencies.getMegaphoneRepository().markFinished(Megaphones.Event.CLIENT_DEPRECATED, () -> {
                       ThreadUtil.runOnMain(this::finish);
                     });
                   })
                   .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                   .show();
  }
}
