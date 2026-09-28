package dev.chat.fork.messenger.video.interfaces

fun interface TranscoderCancelationSignal {
  fun isCanceled(): Boolean
}
