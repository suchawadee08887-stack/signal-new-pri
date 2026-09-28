package dev.chat.fork.messenger.badges.self.none

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import org.signal.core.ui.BottomSheetUtil
import org.signal.core.util.DimensionUnit
import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.badges.models.BadgePreview
import dev.chat.fork.messenger.components.settings.DSLConfiguration
import dev.chat.fork.messenger.components.settings.DSLSettingsAdapter
import dev.chat.fork.messenger.components.settings.DSLSettingsBottomSheetFragment
import dev.chat.fork.messenger.components.settings.DSLSettingsText
import dev.chat.fork.messenger.components.settings.app.AppSettingsActivity
import dev.chat.fork.messenger.components.settings.configure
import org.signal.core.ui.R as CoreUiR

class BecomeASustainerFragment : DSLSettingsBottomSheetFragment() {

  private val viewModel: BecomeASustainerViewModel by viewModels()

  override fun bindAdapter(adapter: DSLSettingsAdapter) {
    BadgePreview.register(adapter)

    viewModel.state.observe(viewLifecycleOwner) {
      adapter.submitList(getConfiguration(it).toMappingModelList())
    }
  }

  private fun getConfiguration(state: BecomeASustainerState): DSLConfiguration {
    return configure {
      customPref(BadgePreview.BadgeModel.FeaturedModel(badge = state.badge))

      sectionHeaderPref(
        title = DSLSettingsText.from(
          R.string.BecomeASustainerFragment__get_badges,
          DSLSettingsText.CenterModifier,
          DSLSettingsText.TitleLargeModifier
        )
      )

      space(DimensionUnit.DP.toPixels(8f).toInt())

      noPadTextPref(
        title = DSLSettingsText.from(
          R.string.BecomeASustainerFragment__signal_is_a_non_profit,
          DSLSettingsText.CenterModifier,
          DSLSettingsText.TextAppearanceModifier(CoreUiR.style.Signal_Text_BodyMedium),
          DSLSettingsText.ColorModifier(ContextCompat.getColor(requireContext(), CoreUiR.color.signal_colorOnSurfaceVariant))
        )
      )

      space(DimensionUnit.DP.toPixels(32f).toInt())

      tonalWrappedButton(
        text = DSLSettingsText.from(
          R.string.BecomeASustainerMegaphone__become_a_sustainer
        ),
        onClick = {
          requireActivity().finish()
          requireActivity().startActivity(AppSettingsActivity.subscriptions(requireContext()).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
        }
      )

      space(DimensionUnit.DP.toPixels(32f).toInt())
    }
  }

  companion object {
    @JvmStatic
    fun show(fragmentManager: FragmentManager) {
      BecomeASustainerFragment().show(fragmentManager, BottomSheetUtil.STANDARD_BOTTOM_SHEET_FRAGMENT_TAG)
    }
  }
}
