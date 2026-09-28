package dev.chat.fork.messenger.profiles.manage

import dev.chat.fork.messenger.databinding.CopyButtonBinding
import dev.chat.fork.messenger.util.adapter.mapping.BindingFactory
import dev.chat.fork.messenger.util.adapter.mapping.BindingViewHolder
import dev.chat.fork.messenger.util.adapter.mapping.MappingAdapter
import dev.chat.fork.messenger.util.adapter.mapping.MappingModel

/**
 * Outlined button that allows the user to copy a piece of data.
 */
object CopyButton {
  fun register(mappingAdapter: MappingAdapter) {
    mappingAdapter.registerFactory(Model::class.java, BindingFactory(::ViewHolder, CopyButtonBinding::inflate))
  }

  class Model(
    val text: CharSequence,
    val onClick: (Model) -> Unit
  ) : MappingModel<Model> {
    override fun areItemsTheSame(newItem: Model): Boolean = true

    override fun areContentsTheSame(newItem: Model): Boolean = text == newItem.text
  }

  private class ViewHolder(binding: CopyButtonBinding) : BindingViewHolder<Model, CopyButtonBinding>(binding) {
    override fun bind(model: Model) {
      binding.root.text = model.text
      binding.root.setOnClickListener { model.onClick(model) }
    }
  }
}
