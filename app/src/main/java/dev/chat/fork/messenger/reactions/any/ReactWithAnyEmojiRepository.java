package dev.chat.fork.messenger.reactions.any;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.stream.Collectors;

import org.signal.core.util.ThreadUtil;
import org.signal.core.util.concurrent.SignalExecutors;
import org.signal.core.util.logging.Log;
import dev.chat.fork.messenger.R;
import dev.chat.fork.messenger.components.emoji.RecentEmojiPageModel;
import dev.chat.fork.messenger.database.SignalDatabase;
import dev.chat.fork.messenger.database.model.MessageId;
import dev.chat.fork.messenger.database.model.ReactionRecord;
import dev.chat.fork.messenger.emoji.EmojiCategory;
import dev.chat.fork.messenger.emoji.EmojiSource;
import dev.chat.fork.messenger.reactions.ReactionDetails;
import dev.chat.fork.messenger.recipients.Recipient;
import dev.chat.fork.messenger.sms.MessageSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

final class ReactWithAnyEmojiRepository {

  private static final String TAG = Log.tag(ReactWithAnyEmojiRepository.class);

  private final Context                     context;
  private final RecentEmojiPageModel        recentEmojiPageModel;
  private final List<ReactWithAnyEmojiPage> emojiPages;

  ReactWithAnyEmojiRepository(@NonNull Context context, @NonNull String storageKey) {
    this.context              = context;
    this.recentEmojiPageModel = new RecentEmojiPageModel(context, storageKey);
    this.emojiPages           = new LinkedList<>();

    emojiPages.addAll(EmojiSource.getLatest().getDisplayPages().stream()
                                 .filter(p -> p.getIconAttr() != EmojiCategory.EMOTICONS.getIcon())
                                 .map(page -> new ReactWithAnyEmojiPage(Collections.singletonList(new ReactWithAnyEmojiPageBlock(EmojiCategory.getCategoryLabel(page.getIconAttr()), page))))
                                 .collect(Collectors.toList()));
  }

  List<ReactWithAnyEmojiPage> getEmojiPageModels(@NonNull List<ReactionDetails> thisMessagesReactions) {
    List<ReactWithAnyEmojiPage> pages       = new LinkedList<>();
    List<String>                thisMessage = thisMessagesReactions.stream()
                                                                   .map(ReactionDetails::getDisplayEmoji)
                                                                   .distinct().collect(Collectors.toList());

    if (thisMessage.isEmpty()) {
      pages.add(new ReactWithAnyEmojiPage(Collections.singletonList(new ReactWithAnyEmojiPageBlock(R.string.ReactWithAnyEmojiBottomSheetDialogFragment__recently_used, recentEmojiPageModel))));
    } else {
      pages.add(new ReactWithAnyEmojiPage(Arrays.asList(new ReactWithAnyEmojiPageBlock(R.string.ReactWithAnyEmojiBottomSheetDialogFragment__this_message, new ThisMessageEmojiPageModel(thisMessage)),
                                                        new ReactWithAnyEmojiPageBlock(R.string.ReactWithAnyEmojiBottomSheetDialogFragment__recently_used, recentEmojiPageModel))));
    }

    pages.addAll(emojiPages);

    return pages;
  }

  void addEmojiToMessage(@NonNull String emoji, @NonNull MessageId messageId) {
    SignalExecutors.BOUNDED.execute(() -> {
      ReactionRecord  oldRecord = SignalDatabase.reactions().getReactions(messageId).stream()
                                                .filter(record -> record.getAuthor().equals(Recipient.self().getId()))
                                                .findFirst()
                                                .orElse(null);

      if (oldRecord != null && oldRecord.getEmoji().equals(emoji)) {
        MessageSender.sendReactionRemoval(context, messageId, oldRecord);
      } else {
        MessageSender.sendNewReaction(context, messageId, emoji);
        ThreadUtil.runOnMain(() -> recentEmojiPageModel.onCodePointSelected(emoji));
      }
    });
  }
}
