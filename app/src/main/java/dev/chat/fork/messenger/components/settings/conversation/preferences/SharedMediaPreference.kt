package dev.chat.fork.messenger.components.settings.conversation.preferences

import android.view.View
import com.bumptech.glide.Glide
import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.components.ThreadPhotoRailView
import dev.chat.fork.messenger.components.settings.PreferenceModel
import dev.chat.fork.messenger.database.MediaTable
import dev.chat.fork.messenger.util.ViewUtil
import dev.chat.fork.messenger.util.adapter.mapping.LayoutFactory
import dev.chat.fork.messenger.util.adapter.mapping.MappingAdapter
import dev.chat.fork.messenger.util.adapter.mapping.MappingViewHolder

/**
 * Renders the shared media photo rail.
 */
object SharedMediaPreference {

  fun register(adapter: MappingAdapter) {
    adapter.registerFactory(Model::class.java, LayoutFactory(::ViewHolder, R.layout.conversation_settings_shared_media))
  }

  class Model(
    val mediaRecords: List<MediaTable.MediaRecord>,
    val mediaIds: List<Long>,
    val onMediaRecordClick: (View, MediaTable.MediaRecord, Boolean) -> Unit
  ) : PreferenceModel<Model>() {
    override fun areItemsTheSame(newItem: Model): Boolean {
      return true
    }

    override fun areContentsTheSame(newItem: Model): Boolean {
      return super.areContentsTheSame(newItem) &&
        mediaIds == newItem.mediaIds
    }
  }

  private class ViewHolder(itemView: View) : MappingViewHolder<Model>(itemView) {

    private val rail: ThreadPhotoRailView = itemView.findViewById(R.id.rail_view)

    override fun bind(model: Model) {
      rail.setMediaRecords(Glide.with(rail), model.mediaRecords)
      rail.setListener { v, m ->
        model.onMediaRecordClick(v, m, ViewUtil.isLtr(rail))
      }
    }
  }
}
