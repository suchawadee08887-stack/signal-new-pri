package dev.chat.fork.messenger.conversationlist;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import dev.chat.fork.messenger.R;
import dev.chat.fork.messenger.conversationlist.model.Conversation;
import dev.chat.fork.messenger.conversationlist.model.ConversationReader;

class ClearFilterViewHolder extends RecyclerView.ViewHolder {

  private final View tip;

  ClearFilterViewHolder(@NonNull View itemView, OnClearFilterClickListener listener) {
    super(itemView);
    tip = itemView.findViewById(R.id.clear_filter_tip);
    itemView.findViewById(R.id.clear_filter).setOnClickListener(v -> {
      listener.onClearFilterClick();
    });
  }

  void bind(@NonNull Conversation conversation) {
    if (conversation.getThreadRecord().getType() == ConversationReader.TYPE_SHOW_TIP) {
      tip.setVisibility(View.VISIBLE);
    } else {
      tip.setVisibility(View.GONE);
    }
  }

  interface OnClearFilterClickListener {
    void onClearFilterClick();
  }
}
