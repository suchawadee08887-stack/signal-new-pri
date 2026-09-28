package dev.chat.fork.messenger.badges.models

import android.view.View
import android.widget.TextView
import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.badges.BadgeImageView
import dev.chat.fork.messenger.util.adapter.mapping.LayoutFactory
import dev.chat.fork.messenger.util.adapter.mapping.MappingAdapter
import dev.chat.fork.messenger.util.adapter.mapping.MappingModel
import dev.chat.fork.messenger.util.adapter.mapping.MappingViewHolder

/**
 * Displays a 160dp badge.
 */
object BadgeDisplay160 {
  fun register(mappingAdapter: MappingAdapter) {
    mappingAdapter.registerFactory(Model::class.java, LayoutFactory(::ViewHolder, R.layout.badge_display_160))
  }

  class Model(val badge: Badge) : MappingModel<Model> {
    override fun areItemsTheSame(newItem: Model): Boolean = badge.id == newItem.badge.id

    override fun areContentsTheSame(newItem: Model): Boolean = badge == newItem.badge
  }

  class ViewHolder(itemView: View) : MappingViewHolder<Model>(itemView) {
    private val badgeImageView: BadgeImageView = itemView.findViewById(R.id.badge)
    private val titleView: TextView = itemView.findViewById(R.id.name)

    override fun bind(model: Model) {
      titleView.text = model.badge.name
      badgeImageView.setBadge(model.badge)
    }
  }
}
