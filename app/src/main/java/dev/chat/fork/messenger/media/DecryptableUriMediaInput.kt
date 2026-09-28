package dev.chat.fork.messenger.media

import android.content.Context
import android.net.Uri
import androidx.annotation.RequiresApi
import dev.chat.fork.messenger.database.SignalDatabase.Companion.attachments
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.mms.PartAuthority
import dev.chat.fork.messenger.mms.PartUriParser
import dev.chat.fork.messenger.video.MediaDataSourceProvider
import dev.chat.fork.messenger.video.interfaces.MediaInput
import dev.chat.fork.messenger.video.interfaces.MediaInputFactory
import dev.chat.fork.messenger.video.videoconverter.mediadatasource.MediaDataSourceMediaInput
import java.io.IOException

/**
 * A media input source that is decrypted on the fly.
 */
@RequiresApi(api = 23)
object DecryptableUriMediaInput : MediaInputFactory {
  @Throws(IOException::class)
  override fun createForUri(context: Context, uri: Uri): MediaInput {
    if (AppDependencies.blobs.isAuthority(uri)) {
      return MediaDataSourceMediaInput(MediaDataSourceProvider.getMediaDataSource(context, uri))
    }
    return if (PartAuthority.isLocalUri(uri)) {
      createForAttachmentUri(uri)
    } else {
      UriMediaInput(context, uri)
    }
  }

  private fun createForAttachmentUri(uri: Uri): MediaInput {
    val partId = PartUriParser(uri).partId
    if (!partId.isValid) {
      throw AssertionError()
    }
    val mediaDataSource = attachments.mediaDataSourceFor(partId, true) ?: throw AssertionError()
    return MediaDataSourceMediaInput(mediaDataSource)
  }
}
