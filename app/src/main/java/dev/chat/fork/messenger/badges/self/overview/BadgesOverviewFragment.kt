package dev.chat.fork.messenger.badges.self.overview

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.signal.core.util.concurrent.LifecycleDisposable
import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.badges.BadgeRepository
import dev.chat.fork.messenger.badges.Badges
import dev.chat.fork.messenger.badges.Badges.displayBadges
import dev.chat.fork.messenger.badges.models.Badge
import dev.chat.fork.messenger.badges.view.ViewBadgeBottomSheetDialogFragment
import dev.chat.fork.messenger.components.settings.DSLConfiguration
import dev.chat.fork.messenger.components.settings.DSLSettingsFragment
import dev.chat.fork.messenger.components.settings.DSLSettingsText
import dev.chat.fork.messenger.components.settings.configure
import dev.chat.fork.messenger.recipients.Recipient
import dev.chat.fork.messenger.util.adapter.mapping.MappingAdapter
import dev.chat.fork.messenger.util.navigation.safeNavigate

/**
 * Fragment to allow user to manage options related to the badges they've unlocked.
 */
class BadgesOverviewFragment : DSLSettingsFragment(
  titleId = R.string.ManageProfileFragment_badges,
  layoutManagerProducer = Badges::createLayoutManagerForGridWithBadges
) {

  private val lifecycleDisposable = LifecycleDisposable()
  private val viewModel: BadgesOverviewViewModel by viewModels(
    factoryProducer = {
      BadgesOverviewViewModel.Factory(BadgeRepository(requireContext()))
    }
  )

  override fun bindAdapter(adapter: MappingAdapter) {
    Badge.register(adapter) { badge, _, isFaded ->
      if (badge.isExpired() || isFaded) {
        findNavController().safeNavigate(BadgesOverviewFragmentDirections.actionBadgeManageFragmentToExpiredBadgeDialog(badge, null, null))
      } else {
        ViewBadgeBottomSheetDialogFragment.show(parentFragmentManager, Recipient.self().id, badge)
      }
    }

    lifecycleDisposable.bindTo(viewLifecycleOwner.lifecycle)

    viewModel.state.observe(viewLifecycleOwner) { state ->
      adapter.submitList(getConfiguration(state).toMappingModelList())
    }

    lifecycleDisposable.add(
      viewModel.events.subscribe { event: BadgesOverviewEvent ->
        when (event) {
          BadgesOverviewEvent.FAILED_TO_UPDATE_PROFILE -> Toast.makeText(requireContext(), R.string.BadgesOverviewFragment__failed_to_update_profile, Toast.LENGTH_LONG).show()
        }
      }
    )
  }

  private fun getConfiguration(state: BadgesOverviewState): DSLConfiguration {
    return configure {
      sectionHeaderPref(R.string.BadgesOverviewFragment__my_badges)

      displayBadges(
        context = requireContext(),
        badges = state.allUnlockedBadges,
        fadedBadgeId = state.fadedBadgeId
      )

      asyncSwitchPref(
        title = DSLSettingsText.from(R.string.BadgesOverviewFragment__display_badges_on_profile),
        isChecked = state.displayBadgesOnProfile,
        isEnabled = state.stage == BadgesOverviewState.Stage.READY && state.hasUnexpiredBadges && state.hasInternet,
        isProcessing = state.stage == BadgesOverviewState.Stage.UPDATING_BADGE_DISPLAY_STATE,
        onClick = {
          viewModel.setDisplayBadgesOnProfile(!state.displayBadgesOnProfile)
        }
      )

      clickPref(
        title = DSLSettingsText.from(R.string.BadgesOverviewFragment__featured_badge),
        summary = state.featuredBadge?.name?.let { DSLSettingsText.from(it) },
        isEnabled = state.stage == BadgesOverviewState.Stage.READY && state.hasUnexpiredBadges && state.hasInternet,
        onClick = {
          findNavController().safeNavigate(BadgesOverviewFragmentDirections.actionBadgeManageFragmentToFeaturedBadgeFragment())
        }
      )
    }
  }
}
