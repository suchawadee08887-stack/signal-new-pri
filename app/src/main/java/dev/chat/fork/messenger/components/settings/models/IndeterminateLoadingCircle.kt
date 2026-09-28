package dev.chat.fork.messenger.components.settings.models

import android.view.View
import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.components.settings.PreferenceModel
import dev.chat.fork.messenger.util.adapter.mapping.LayoutFactory
import dev.chat.fork.messenger.util.adapter.mapping.MappingAdapter
import dev.chat.fork.messenger.util.adapter.mapping.MappingViewHolder

object IndeterminateLoadingCircle : PreferenceModel<IndeterminateLoadingCircle>() {
  override fun areItemsTheSame(newItem: IndeterminateLoadingCircle): Boolean = true

  private class ViewHolder(itemView: View) : MappingViewHolder<IndeterminateLoadingCircle>(itemView) {
    override fun bind(model: IndeterminateLoadingCircle) = Unit
  }

  fun register(mappingAdapter: MappingAdapter) {
    mappingAdapter.registerFactory(IndeterminateLoadingCircle::class.java, LayoutFactory({ ViewHolder(it) }, R.layout.indeterminate_loading_circle_pref))
  }
}
