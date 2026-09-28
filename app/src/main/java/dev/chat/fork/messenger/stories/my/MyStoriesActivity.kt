package dev.chat.fork.messenger.stories.my

import androidx.fragment.app.Fragment
import dev.chat.fork.messenger.components.FragmentWrapperActivity

class MyStoriesActivity : FragmentWrapperActivity() {
  override fun getFragment(): Fragment {
    return MyStoriesFragment()
  }
}
