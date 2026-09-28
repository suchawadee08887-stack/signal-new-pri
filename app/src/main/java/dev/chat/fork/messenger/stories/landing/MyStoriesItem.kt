package dev.chat.fork.messenger.stories.landing

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.avatar.view.AvatarView
import dev.chat.fork.messenger.components.settings.PreferenceModel
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.util.adapter.mapping.LayoutFactory
import dev.chat.fork.messenger.util.adapter.mapping.MappingAdapter
import dev.chat.fork.messenger.util.adapter.mapping.MappingViewHolder

/**
 * Item displayed on an empty Stories landing page allowing the user to add a new story.
 */
object MyStoriesItem {

  fun register(mappingAdapter: MappingAdapter) {
    mappingAdapter.registerFactory(Model::class.java, LayoutFactory(::ViewHolder, R.layout.stories_landing_item_my_stories))
  }

  class Model(
    val lifecycleOwner: LifecycleOwner,
    val onClick: () -> Unit
  ) : PreferenceModel<Model>() {
    override fun areItemsTheSame(newItem: Model): Boolean = true
  }

  private class ViewHolder(itemView: View) : MappingViewHolder<Model>(itemView) {

    private val avatarView: AvatarView = itemView.findViewById(R.id.avatar)

    private var recipient: Recipient? = null

    private val recipientObserver = object : Observer<Recipient> {
      override fun onChanged(recipient: Recipient) {
        onRecipientChanged(recipient)
      }
    }

    override fun bind(model: Model) {
      itemView.setOnClickListener { model.onClick() }
      observeRecipient(model.lifecycleOwner, Recipient.self())
    }

    private fun onRecipientChanged(recipient: Recipient) {
      avatarView.displayProfileAvatar(recipient)
    }

    private fun observeRecipient(lifecycleOwner: LifecycleOwner?, recipient: Recipient?) {
      this.recipient?.live()?.liveData?.removeObserver(recipientObserver)

      this.recipient = recipient

      lifecycleOwner?.let {
        this.recipient?.live()?.liveData?.observe(lifecycleOwner, recipientObserver)
      }
    }

    override fun onViewRecycled() {
      unbindRecipient()
    }

    private fun unbindRecipient() {
      observeRecipient(null, null)
    }
  }
}
