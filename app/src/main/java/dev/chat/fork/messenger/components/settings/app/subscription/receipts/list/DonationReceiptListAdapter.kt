package dev.chat.fork.messenger.components.settings.app.subscription.receipts.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.components.settings.DSLSettingsText
import dev.chat.fork.messenger.components.settings.SectionHeaderPreference
import dev.chat.fork.messenger.components.settings.SectionHeaderPreferenceViewHolder
import dev.chat.fork.messenger.components.settings.TextPreference
import dev.chat.fork.messenger.components.settings.TextPreferenceViewHolder
import dev.chat.fork.messenger.util.StickyHeaderDecoration
import dev.chat.fork.messenger.util.adapter.mapping.LayoutFactory
import dev.chat.fork.messenger.util.adapter.mapping.MappingAdapter
import dev.chat.fork.messenger.util.toLocalDateTime
import org.signal.core.ui.R as CoreUiR

class DonationReceiptListAdapter(onModelClick: (DonationReceiptListItem.Model) -> Unit) : MappingAdapter(), StickyHeaderDecoration.StickyHeaderAdapter<SectionHeaderPreferenceViewHolder> {

  init {
    registerFactory(TextPreference::class.java, LayoutFactory({ TextPreferenceViewHolder(it) }, R.layout.dsl_preference_item))
    DonationReceiptListItem.register(this, onModelClick)
  }

  override fun getHeaderId(position: Int): Long {
    return when (val item = getItem(position)) {
      is DonationReceiptListItem.Model -> item.record.timestamp.toLocalDateTime().year.toLong()
      else -> StickyHeaderDecoration.StickyHeaderAdapter.NO_HEADER_ID
    }
  }

  override fun onCreateHeaderViewHolder(parent: ViewGroup?, position: Int, type: Int): SectionHeaderPreferenceViewHolder {
    return SectionHeaderPreferenceViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.dsl_section_header, parent, false))
  }

  override fun onBindHeaderViewHolder(viewHolder: SectionHeaderPreferenceViewHolder?, position: Int, type: Int) {
    viewHolder?.itemView?.run {
      val color = ContextCompat.getColor(context, CoreUiR.color.signal_colorBackground)
      setBackgroundColor(color)
    }

    viewHolder?.bind(SectionHeaderPreference(DSLSettingsText.from(getHeaderId(position).toString())))
  }
}
