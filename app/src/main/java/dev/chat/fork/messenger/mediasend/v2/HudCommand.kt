package dev.chat.fork.messenger.mediasend.v2

sealed class HudCommand {
  object StartDraw : HudCommand()
  object StartCropAndRotate : HudCommand()
  object SaveMedia : HudCommand()

  object GoToText : HudCommand()
  object GoToReview : HudCommand()

  object ResumeEntryTransition : HudCommand()
}
