package org.signal.camera.demo

import android.app.Application
import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import org.signal.camera.CameraDependencies
import org.signal.core.util.logging.AndroidLogger
import org.signal.core.util.logging.Log
import dev.chat.fork.messenger.mms.RegisterGlideComponents
import dev.chat.fork.messenger.mms.SignalGlideModule

/**
 * Application class for the camera demo.
 */
class CameraDemoApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    Log.initialize(AndroidLogger)
    SignalGlideModule.registerGlideComponents = object : RegisterGlideComponents {
      override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
      }
    }

    CameraDependencies.init(
      this,
      object : CameraDependencies.Provider {
        override fun isStoriesFeatureEnabled(): Boolean = false
      }
    )
  }
}
