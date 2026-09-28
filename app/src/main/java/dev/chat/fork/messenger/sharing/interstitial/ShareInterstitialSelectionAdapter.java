package dev.chat.fork.messenger.sharing.interstitial;

import dev.chat.fork.messenger.R;
import dev.chat.fork.messenger.util.adapter.mapping.MappingAdapter;
import dev.chat.fork.messenger.util.viewholders.RecipientViewHolder;

class ShareInterstitialSelectionAdapter extends MappingAdapter {
  ShareInterstitialSelectionAdapter() {
    registerFactory(ShareInterstitialMappingModel.class, RecipientViewHolder.createFactory(R.layout.share_contact_selection_item, null));
  }
}
