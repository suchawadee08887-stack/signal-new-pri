/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package dev.chat.fork.messenger.registration.ui.captcha

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import org.signal.core.ui.logging.LoggingFragment
import dev.chat.fork.messenger.BuildConfig
import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.components.ViewBinderDelegate
import dev.chat.fork.messenger.databinding.FragmentRegistrationCaptchaBinding
import dev.chat.fork.messenger.registration.fragments.RegistrationConstants
import dev.chat.fork.messenger.util.SystemWindowInsetsSetter

abstract class CaptchaFragment : LoggingFragment(R.layout.fragment_registration_captcha) {

  private val binding: FragmentRegistrationCaptchaBinding by ViewBinderDelegate(FragmentRegistrationCaptchaBinding::bind)

  @SuppressLint("SetJavaScriptEnabled")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    SystemWindowInsetsSetter.attach(view, viewLifecycleOwner)
    binding.registrationCaptchaWebView.settings.javaScriptEnabled = true
    binding.registrationCaptchaWebView.clearCache(true)

    binding.registrationCaptchaWebView.webViewClient = object : WebViewClient() {
      @Deprecated("Deprecated in Java")
      override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        if (url.startsWith(RegistrationConstants.SIGNAL_CAPTCHA_SCHEME)) {
          val token = url.substring(RegistrationConstants.SIGNAL_CAPTCHA_SCHEME.length)
          handleCaptchaToken(token)
          findNavController().navigateUp()
          return true
        }
        return false
      }
    }
    binding.registrationCaptchaWebView.loadUrl(BuildConfig.SIGNAL_CAPTCHA_URL)
  }

  abstract fun handleCaptchaToken(token: String)
}
