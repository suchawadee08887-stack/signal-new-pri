package dev.chat.fork.messenger.groups.ui.creategroup.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import dev.chat.fork.messenger.PassphraseRequiredActivity;
import dev.chat.fork.messenger.R;
import dev.chat.fork.messenger.conversation.ConversationIntents;
import dev.chat.fork.messenger.groups.ui.managegroup.dialogs.GroupInviteSentDialog;
import dev.chat.fork.messenger.recipients.Recipient;
import dev.chat.fork.messenger.recipients.RecipientId;
import dev.chat.fork.messenger.util.DynamicNoActionBarTheme;
import dev.chat.fork.messenger.util.DynamicTheme;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AddGroupDetailsActivity extends PassphraseRequiredActivity implements AddGroupDetailsFragment.Callback {

  private static final String EXTRA_RECIPIENTS = "recipient_ids";

  private final DynamicTheme theme = new DynamicNoActionBarTheme();

  public static Intent newIntent(@NonNull Context context, @NonNull Collection<RecipientId> recipients) {
    Intent intent = new Intent(context, AddGroupDetailsActivity.class);

    intent.putParcelableArrayListExtra(EXTRA_RECIPIENTS, new ArrayList<>(recipients));

    return intent;
  }

  @Override
  protected void onCreate(@Nullable Bundle bundle, boolean ready) {
    theme.onCreate(this);

    setContentView(R.layout.add_group_details_activity);

    if (bundle == null) {
      ArrayList<RecipientId>      recipientIds = getIntent().getParcelableArrayListExtra(EXTRA_RECIPIENTS);
      AddGroupDetailsFragmentArgs arguments    = new AddGroupDetailsFragmentArgs.Builder(recipientIds.toArray(new RecipientId[0])).build();
      NavHostFragment             fragment     = NavHostFragment.create(R.navigation.create_group, arguments.toBundle());

      getSupportFragmentManager().beginTransaction()
                                 .replace(R.id.nav_host_fragment, fragment)
                                 .commit();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    theme.onResume(this);
  }

  @Override
  public void onGroupCreated(@NonNull RecipientId recipientId,
                             long threadId,
                             @NonNull List<Recipient> invitedMembers)
  {
    if (invitedMembers.isEmpty()) {
      goToConversation(recipientId, threadId);
    } else {
      getSupportFragmentManager().setFragmentResultListener(
          GroupInviteSentDialog.RESULT_DISMISSED,
          this,
          (requestKey, result) -> goToConversation(recipientId, threadId)
      );
      GroupInviteSentDialog.show(getSupportFragmentManager(), invitedMembers);
    }
  }

  void goToConversation(@NonNull RecipientId recipientId, long threadId) {
    Intent intent = ConversationIntents.createBuilderSync(this, recipientId, threadId)
                                       .firstTimeInSelfCreatedGroup()
                                       .build();

    startActivity(intent);
    setResult(RESULT_OK);
    finish();
  }

  @Override
  public void onNavigationButtonPressed() {
    setResult(RESULT_CANCELED);
    finish();
  }
}
