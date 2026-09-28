package dev.chat.fork.messenger;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import org.signal.core.util.concurrent.LifecycleDisposable;
import dev.chat.fork.messenger.components.settings.app.AppSettingsActivity;
import dev.chat.fork.messenger.conversation.ConversationIntents;
import dev.chat.fork.messenger.groups.ui.creategroup.CreateGroupActivity;
import dev.chat.fork.messenger.main.MainNavigationDetailLocation;
import dev.chat.fork.messenger.main.MainNavigationViewModel;
import dev.chat.fork.messenger.recipients.RecipientId;

import io.reactivex.rxjava3.disposables.Disposable;

public class MainNavigator {

  public static final int REQUEST_CONFIG_CHANGES = 901;

  private final AppCompatActivity       activity;
  private final LifecycleDisposable     lifecycleDisposable;
  private final MainNavigationViewModel viewModel;

  public MainNavigator(@NonNull AppCompatActivity activity, @NonNull MainNavigationViewModel viewModel) {
    this.activity            = activity;
    this.lifecycleDisposable = new LifecycleDisposable();
    this.viewModel           = viewModel;

    lifecycleDisposable.bindTo(activity);
  }

  public static MainNavigator get(@NonNull Activity activity) {
    if (!(activity instanceof MainActivity)) {
      throw new IllegalArgumentException("Activity must be an instance of MainActivity!");
    }

    return ((NavigatorProvider) activity).getNavigator();
  }

  public void goToConversation(@NonNull RecipientId recipientId, long threadId, int distributionType, int startingPosition) {
    goToConversation(recipientId, threadId, distributionType, startingPosition, false);
  }

  public void goToConversation(@NonNull RecipientId recipientId, long threadId, int distributionType, int startingPosition, boolean incognito) {
    Disposable disposable = ConversationIntents.createBuilder(activity, recipientId, threadId)
                                               .map(builder -> builder.withDistributionType(distributionType)
                                                                      .withStartingPosition(startingPosition)
                                                                      .asIncognito(incognito)
                                                                      .toConversationArgs())
                                               .subscribe(args -> viewModel.goTo(new MainNavigationDetailLocation.Conversation(args)));

    lifecycleDisposable.add(disposable);
  }

  public void goToAppSettings() {
    activity.startActivityForResult(AppSettingsActivity.home(activity), REQUEST_CONFIG_CHANGES);
  }

  public void goToGroupCreation() {
    activity.startActivity(CreateGroupActivity.createIntent(activity));
  }

  private @NonNull FragmentManager getFragmentManager() {
    return activity.getSupportFragmentManager();
  }

  public interface BackHandler {
    /**
     * @return True if the back pressed was handled in our own custom way, false if it should be given
     * to the system to do the default behavior.
     */
    boolean onBackPressed();
  }

  public interface NavigatorProvider {
    @NonNull MainNavigator getNavigator();
    void onFirstRender();
  }
}
