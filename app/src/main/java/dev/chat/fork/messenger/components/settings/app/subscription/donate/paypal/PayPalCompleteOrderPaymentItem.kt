package dev.chat.fork.messenger.components.settings.app.subscription.donate.paypal

import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.util.adapter.mapping.LayoutFactory
import dev.chat.fork.messenger.util.adapter.mapping.MappingAdapter
import dev.chat.fork.messenger.util.adapter.mapping.MappingModel
import dev.chat.fork.messenger.util.adapter.mapping.MappingViewHolder.SimpleViewHolder

/**
 * Line item on the PayPal order confirmation screen.
 */
object PayPalCompleteOrderPaymentItem {
  fun register(mappingAdapter: MappingAdapter) {
    mappingAdapter.registerFactory(Model::class.java, LayoutFactory(::SimpleViewHolder, R.layout.paypal_complete_order_payment_item))
  }

  class Model : MappingModel<Model> {
    override fun areItemsTheSame(newItem: Model): Boolean = true

    override fun areContentsTheSame(newItem: Model): Boolean = true
  }
}
