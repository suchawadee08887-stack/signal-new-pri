package dev.chat.fork.messenger.notifications

import android.app.Application
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import dev.chat.fork.messenger.database.MessageTable.ExpirationInfo
import dev.chat.fork.messenger.database.MessageTable.MarkedMessageInfo
import dev.chat.fork.messenger.database.MessageTable.SyncMessageId
import dev.chat.fork.messenger.database.model.MessageId
import dev.chat.fork.messenger.database.model.StoryType
import dev.chat.fork.messenger.dependencies.AppDependencies
import dev.chat.fork.messenger.jobmanager.Job
import dev.chat.fork.messenger.jobmanager.JobManager
import dev.chat.fork.messenger.jobmanager.JsonJobData
import dev.chat.fork.messenger.jobs.MultiDeviceReadUpdateJob
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.recipients.RecipientId
import dev.chat.fork.messenger.testutil.MockAppDependenciesRule
import dev.chat.fork.messenger.util.TextSecurePreferences
import java.util.LinkedList

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, application = Application::class)
class MarkReadReceiverTest {

  @get:Rule
  val appDependencies = MockAppDependenciesRule()

  private val jobs: MutableList<Job> = LinkedList()

  @Before
  fun setUp() {
    val jobManager: JobManager = AppDependencies.jobManager
    every { jobManager.add(capture(jobs)) } returns Unit

    mockkObject(Recipient)
    every { Recipient.self() } returns Recipient()

    mockkStatic(TextSecurePreferences::class)
    every { TextSecurePreferences.isReadReceiptsEnabled(any()) } returns true
  }

  @After
  fun tearDown() {
    unmockkAll()
  }

  @Test
  fun givenMultipleThreadsWithMultipleMessagesEach_whenIProcess_thenIProperlyGroupByThreadAndRecipient() {
    // GIVEN
    val recipients = (1L until 4L).map { id -> RecipientId.from(id) }
    val threads = (4L until 7L).toList()
    val expected = recipients.size * threads.size + 1
    val infoList = threads.map { threadId -> recipients.map { recipientId -> createMarkedMessageInfo(threadId, recipientId) } }.flatten()

    // WHEN
    MarkReadReceiver.process(infoList + infoList)

    // THEN
    Assert.assertEquals("Should have 10 total jobs, including MultiDeviceReadUpdateJob", expected.toLong(), jobs.size.toLong())

    val threadRecipientPairs: MutableSet<Pair<Long, String>> = HashSet()
    jobs.forEach { job ->
      if (job is MultiDeviceReadUpdateJob) {
        return@forEach
      }
      val data = JsonJobData.deserialize(job.serialize())

      val threadId = data.getLong("thread")
      val recipientId = data.getString("recipient")
      val messageIds = data.getLongArray("message_ids")

      Assert.assertEquals("Each job should contain two messages.", 2, messageIds.size.toLong())
      Assert.assertTrue("Each thread recipient pair should only exist once.", threadRecipientPairs.add(Pair(threadId, recipientId)))
    }

    Assert.assertEquals("Should have 9 total combinations.", 9, threadRecipientPairs.size.toLong())
  }

  private fun createMarkedMessageInfo(threadId: Long, recipientId: RecipientId): MarkedMessageInfo {
    return MarkedMessageInfo(
      threadId,
      SyncMessageId(recipientId, 0),
      MessageId(1),
      ExpirationInfo(0, 0, 0, false),
      StoryType.NONE,
      0
    )
  }
}
