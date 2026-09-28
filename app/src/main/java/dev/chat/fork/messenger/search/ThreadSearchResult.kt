package dev.chat.fork.messenger.search

import dev.chat.fork.messenger.database.model.ThreadWithRecipient

data class ThreadSearchResult(val results: List<ThreadWithRecipient>, val query: String)
