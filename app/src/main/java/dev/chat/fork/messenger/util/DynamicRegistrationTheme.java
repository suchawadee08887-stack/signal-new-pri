package dev.chat.fork.messenger.util;

import androidx.annotation.StyleRes;

import dev.chat.fork.messenger.R;

public class DynamicRegistrationTheme extends DynamicTheme {

  protected @StyleRes int getTheme() {
    return R.style.Signal_DayNight_Registration;
  }
}
