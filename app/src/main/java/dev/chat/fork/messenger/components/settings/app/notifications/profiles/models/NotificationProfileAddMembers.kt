package dev.chat.fork.messenger.components.settings.app.notifications.profiles.models

import android.view.View
import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.components.settings.DSLSettingsIcon
import dev.chat.fork.messenger.components.settings.DSLSettingsText
import dev.chat.fork.messenger.components.settings.NO_TINT
import dev.chat.fork.messenger.components.settings.PreferenceModel
import dev.chat.fork.messenger.components.settings.PreferenceViewHolder
import dev.chat.fork.messenger.recipients.RecipientId
import dev.chat.fork.messenger.util.adapter.mapping.LayoutFactory
import dev.chat.fork.messenger.util.adapter.mapping.MappingAdapter

/**
 * Custom DSL preference for adding members to a profile.
 */
object NotificationProfileAddMembers {

  fun register(adapter: MappingAdapter) {
    adapter.registerFactory(Model::class.java, LayoutFactory(::ViewHolder, R.layout.large_icon_preference_item))
  }

  class Model(
    override val title: DSLSettingsText = DSLSettingsText.from(R.string.AddAllowedMembers__add_people_or_groups),
    override val icon: DSLSettingsIcon = DSLSettingsIcon.from(R.drawable.add_to_a_group, NO_TINT),
    val onClick: (Long, Set<RecipientId>) -> Unit,
    val profileId: Long,
    val currentSelection: Set<RecipientId>
  ) : PreferenceModel<Model>() {
    override fun areContentsTheSame(newItem: Model): Boolean {
      return super.areContentsTheSame(newItem) && profileId == newItem.profileId && currentSelection == newItem.currentSelection
    }
  }

  private class ViewHolder(itemView: View) : PreferenceViewHolder<Model>(itemView) {
    override fun bind(model: Model) {
      super.bind(model)
      itemView.setOnClickListener { model.onClick(model.profileId, model.currentSelection) }
    }
  }
}
