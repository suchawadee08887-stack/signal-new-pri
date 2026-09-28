package dev.chat.fork.messenger

import android.content.ContentValues
import android.os.Build
import org.signal.core.util.logging.AndroidLogger
import org.signal.core.util.logging.Log
import org.signal.spinner.Spinner
import org.signal.spinner.Spinner.DatabaseConfig
import org.signal.spinner.SpinnerLogger
import dev.chat.fork.messenger.database.AttachmentTransformer
import dev.chat.fork.messenger.database.CollapsedStateTransformer
import dev.chat.fork.messenger.database.DatabaseMonitor
import dev.chat.fork.messenger.database.GV2Transformer
import dev.chat.fork.messenger.database.GV2UpdateTransformer
import dev.chat.fork.messenger.database.IdPopupTransformer
import dev.chat.fork.messenger.database.IsStoryTransformer
import dev.chat.fork.messenger.database.JobDatabase
import dev.chat.fork.messenger.database.KeyValueDatabase
import dev.chat.fork.messenger.database.KyberKeyTransformer
import dev.chat.fork.messenger.database.LocalMetricsDatabase
import dev.chat.fork.messenger.database.LogDatabase
import dev.chat.fork.messenger.database.MegaphoneDatabase
import dev.chat.fork.messenger.database.MessageBitmaskColumnTransformer
import dev.chat.fork.messenger.database.MessageRangesTransformer
import dev.chat.fork.messenger.database.PollTransformer
import dev.chat.fork.messenger.database.ProfileKeyCredentialTransformer
import dev.chat.fork.messenger.database.QueryMonitor
import dev.chat.fork.messenger.database.RecipientTransformer
import dev.chat.fork.messenger.database.SignalDatabase
import dev.chat.fork.messenger.database.SignalStoreTransformer
import dev.chat.fork.messenger.database.TimestampTransformer
import dev.chat.fork.messenger.keyvalue.SignalStore
import dev.chat.fork.messenger.logging.PersistentLogger
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.util.AppSignatureUtil
import dev.chat.fork.messenger.util.RemoteConfig
import java.util.Locale

class SpinnerApplicationContext : ApplicationContext() {
  override fun onCreate() {
    super.onCreate()

    try {
      Class.forName("dalvik.system.CloseGuard")
        .getMethod("setEnabled", Boolean::class.javaPrimitiveType)
        .invoke(null, true)
    } catch (e: ReflectiveOperationException) {
      throw RuntimeException(e)
    }

    Spinner.init(
      this,
      mapOf(
        "Device" to { "${Build.MODEL} (Android ${Build.VERSION.RELEASE}, API ${Build.VERSION.SDK_INT})" },
        "Package" to { "$packageName (${AppSignatureUtil.getAppSignature(this)})" },
        "App Version" to { "${BuildConfig.VERSION_NAME} (${BuildConfig.CANONICAL_VERSION_CODE}, ${BuildConfig.GIT_HASH})" },
        "Profile Name" to { (if (SignalStore.account.isRegistered) Recipient.self().profileName.toString() else "none") },
        "E164" to { SignalStore.account.e164 ?: "none" },
        "ACI" to { SignalStore.account.aci?.toString() ?: "none" },
        "PNI" to { SignalStore.account.pni?.toString() ?: "none" },
        Spinner.KEY_ENVIRONMENT to { BuildConfig.FLAVOR_environment.uppercase(Locale.US) }
      ),
      linkedMapOf(
        "signal" to DatabaseConfig(
          db = { SignalDatabase.rawDatabase },
          columnTransformers = listOf(
            MessageBitmaskColumnTransformer,
            GV2Transformer,
            GV2UpdateTransformer,
            IsStoryTransformer,
            TimestampTransformer,
            ProfileKeyCredentialTransformer,
            MessageRangesTransformer,
            KyberKeyTransformer,
            RecipientTransformer,
            AttachmentTransformer,
            PollTransformer,
            IdPopupTransformer,
            CollapsedStateTransformer
          )
        ),
        "jobmanager" to DatabaseConfig(db = { JobDatabase.getInstance(this).sqlCipherDatabase }, columnTransformers = listOf(TimestampTransformer)),
        "keyvalue" to DatabaseConfig(db = { KeyValueDatabase.getInstance(this).sqlCipherDatabase }, columnTransformers = listOf(SignalStoreTransformer)),
        "megaphones" to DatabaseConfig(db = { MegaphoneDatabase.getInstance(this).sqlCipherDatabase }),
        "localmetrics" to DatabaseConfig(db = { LocalMetricsDatabase.getInstance(this).sqlCipherDatabase }),
        "logs" to DatabaseConfig(
          db = { LogDatabase.getInstance(this).sqlCipherDatabase },
          columnTransformers = listOf(TimestampTransformer)
        )
      ),
      linkedMapOf(
        StorageServicePlugin.PATH to StorageServicePlugin(),
        AttachmentPlugin.PATH to AttachmentPlugin(),
        BackupPlugin.PATH to BackupPlugin(),
        ApiPlugin.PATH to ApiPlugin()
      )
    )

    Log.initialize({ RemoteConfig.internalUser }, AndroidLogger, PersistentLogger.getInstance(this), SpinnerLogger)

    DatabaseMonitor.initialize(object : QueryMonitor {
      override fun onSql(sql: String, args: Array<Any>?) {
        Spinner.onSql("signal", sql, args)
      }

      override fun onQuery(distinct: Boolean, table: String, projection: Array<String>?, selection: String?, args: Array<Any>?, groupBy: String?, having: String?, orderBy: String?, limit: String?) {
        Spinner.onQuery("signal", distinct, table, projection, selection, args, groupBy, having, orderBy, limit)
      }

      override fun onDelete(table: String, selection: String?, args: Array<Any>?) {
        Spinner.onDelete("signal", table, selection, args)
      }

      override fun onUpdate(table: String, values: ContentValues, selection: String?, args: Array<Any>?) {
        Spinner.onUpdate("signal", table, values, selection, args)
      }
    })
  }
}
