package dev.chat.fork.messenger.components.settings.conversation

import dev.chat.fork.messenger.util.DynamicNoActionBarTheme
import dev.chat.fork.messenger.util.DynamicTheme

class CallInfoActivity : ConversationSettingsActivity(), ConversationSettingsFragment.TransitionCallback {

  override val dynamicTheme: DynamicTheme = DynamicNoActionBarTheme()
}
