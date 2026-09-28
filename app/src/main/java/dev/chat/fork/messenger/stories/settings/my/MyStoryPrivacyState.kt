package dev.chat.fork.messenger.stories.settings.my

import dev.chat.fork.messenger.database.model.DistributionListPrivacyMode

data class MyStoryPrivacyState(val privacyMode: DistributionListPrivacyMode? = null, val connectionCount: Int = 0)
