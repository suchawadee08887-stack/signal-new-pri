package dev.chat.fork.messenger.stories.viewer.post

import android.graphics.Typeface
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.signal.core.util.Base64
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.database.model.MmsMessageRecord
import dev.chat.fork.messenger.database.model.databaseprotos.StoryTextPost
import dev.chat.fork.messenger.database.withAttachments
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.fonts.TextFont
import dev.chat.fork.messenger.fonts.TextToScript
import dev.chat.fork.messenger.fonts.TypefaceCache

class StoryTextPostRepository {
  fun getRecord(recordId: Long): Single<MmsMessageRecord> {
    return Single.fromCallable {
      SignalDatabase.messages.getMessageRecord(recordId).withAttachments() as MmsMessageRecord
    }.subscribeOn(Schedulers.io())
  }

  fun getTypeface(recordId: Long): Single<Typeface> {
    return getRecord(recordId).flatMap {
      val model = StoryTextPost.ADAPTER.decode(Base64.decode(it.body))
      val textFont = TextFont.fromStyle(model.style)
      val script = TextToScript.guessScript(model.body)

      TypefaceCache.get(AppDependencies.application, textFont, script)
    }
  }
}
