package dev.chat.fork.messenger.conversation.colors

import android.view.ViewGroup
import dev.chat.fork.messenger.util.ProjectionList

/**
 * Denotes that a class can be colorized. The class is responsible for
 * generating its own projection.
 */
interface Colorizable {
  fun getColorizerProjections(coordinateRoot: ViewGroup): ProjectionList
}
