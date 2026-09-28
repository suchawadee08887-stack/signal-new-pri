package dev.chat.fork.messenger.groups.ui.chooseadmin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import java.util.stream.Collectors;

import dev.chat.fork.messenger.MainActivity;
import dev.chat.fork.messenger.PassphraseRequiredActivity;
import dev.chat.fork.messenger.R;
import dev.chat.fork.messenger.groups.BadGroupIdException;
import dev.chat.fork.messenger.groups.GroupId;
import dev.chat.fork.messenger.groups.ui.GroupChangeResult;
import dev.chat.fork.messenger.groups.ui.GroupErrors;
import dev.chat.fork.messenger.groups.ui.GroupMemberEntry;
import dev.chat.fork.messenger.groups.ui.GroupMemberListView;
import dev.chat.fork.messenger.recipients.Recipient;
import dev.chat.fork.messenger.util.DynamicNoActionBarTheme;
import dev.chat.fork.messenger.util.DynamicTheme;
import dev.chat.fork.messenger.util.SystemWindowInsetsSetter;
import dev.chat.fork.messenger.util.views.CircularProgressMaterialButton;

import java.util.Objects;

public final class ChooseNewAdminActivity extends PassphraseRequiredActivity {

  private static final String EXTRA_GROUP_ID = "group_id";

  private ChooseNewAdminViewModel        viewModel;
  private GroupMemberListView            groupList;
  private CircularProgressMaterialButton done;
  private GroupId.V2                     groupId;

  private final DynamicTheme dynamicTheme = new DynamicNoActionBarTheme();

  public static Intent createIntent(@NonNull Context context, @NonNull GroupId.V2 groupId) {
    Intent intent = new Intent(context, ChooseNewAdminActivity.class);
    intent.putExtra(EXTRA_GROUP_ID, groupId.toString());
    return intent;
  }

  @Override
  protected void onPreCreate() {
    dynamicTheme.onCreate(this);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState, boolean ready) {
    super.onCreate(savedInstanceState, ready);
    setContentView(R.layout.choose_new_admin_activity);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    //noinspection ConstantConditions
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    toolbar.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
    SystemWindowInsetsSetter.attach(toolbar, this, WindowInsetsCompat.Type.statusBars());

    try {
      groupId = GroupId.parse(Objects.requireNonNull(getIntent().getStringExtra(EXTRA_GROUP_ID))).requireV2();
    } catch (BadGroupIdException e) {
      throw new AssertionError(e);
    }

    groupList = findViewById(R.id.choose_new_admin_group_list);
    done      = findViewById(R.id.choose_new_admin_done);

    groupList.setClipToPadding(false);
    SystemWindowInsetsSetter.attach(groupList, this, WindowInsetsCompat.Type.navigationBars());
    SystemWindowInsetsSetter.attach(done, this, WindowInsetsCompat.Type.navigationBars(), SystemWindowInsetsSetter.ApplyMode.MARGIN);

    initializeViewModel();

    groupList.initializeAdapter(this);
    groupList.setRecipientSelectionChangeListener(selection -> viewModel.setSelection(selection.stream()
                                                                                               .filter(x -> x instanceof GroupMemberEntry.FullMember)
                                                                                               .map(x-> (GroupMemberEntry.FullMember)x)
                                                                                               .collect(Collectors.toSet())));

    done.setOnClickListener(v -> {
      done.setSpinning();
      viewModel.updateAdminsAndLeave(this::handleUpdateAndLeaveResult);
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    dynamicTheme.onResume(this);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void initializeViewModel() {
    viewModel = new ViewModelProvider(this, new ChooseNewAdminViewModel.Factory(groupId)).get(ChooseNewAdminViewModel.class);

    viewModel.getNonAdminFullMembers().observe(this, groupList::setMembers);
    viewModel.getSelection().observe(this, selection -> done.setVisibility(selection.isEmpty() ? View.GONE : View.VISIBLE));
  }

  private void handleUpdateAndLeaveResult(@NonNull GroupChangeResult updateResult) {
    if (updateResult.isSuccess()) {
      String title = Recipient.externalGroupExact(groupId).getDisplayName(this);
      Toast.makeText(this, getString(R.string.ChooseNewAdminActivity_you_left, title), Toast.LENGTH_LONG).show();
      startActivity(MainActivity.clearTop(this));
      finish();
    } else {
      done.cancelSpinning();
      //noinspection ConstantConditions
      Toast.makeText(this, GroupErrors.getUserDisplayMessage(updateResult.getFailureReason()), Toast.LENGTH_LONG).show();
    }
  }
}
