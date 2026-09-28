package dev.chat.fork.messenger.stories.settings.connections

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import dev.chat.fork.messenger.R
import dev.chat.fork.messenger.components.ViewBinderDelegate
import dev.chat.fork.messenger.components.WrapperDialogFragment
import dev.chat.fork.messenger.contacts.paged.ContactSearchAdapter
import dev.chat.fork.messenger.contacts.paged.ContactSearchConfiguration
import dev.chat.fork.messenger.contacts.paged.ContactSearchPagedDataSourceRepository
import dev.chat.fork.messenger.contacts.paged.ContactSearchRepository
import dev.chat.fork.messenger.contacts.paged.ContactSearchViewModel
import dev.chat.fork.messenger.database.RecipientTable
import dev.chat.fork.messenger.databinding.ViewAllSignalConnectionsFragmentBinding
import dev.chat.fork.messenger.groups.SelectionLimits
import dev.chat.fork.messenger.search.SearchRepository
import dev.chat.fork.messenger.util.SystemWindowInsetsSetter

class ViewAllSignalConnectionsFragment : Fragment(R.layout.view_all_signal_connections_fragment) {

  private val binding by ViewBinderDelegate(ViewAllSignalConnectionsFragmentBinding::bind)

  private val contactSearchViewModel: ContactSearchViewModel by viewModels {
    ContactSearchViewModel.Factory(
      selectionLimits = SelectionLimits(0, 0),
      isMultiSelect = false,
      repository = ContactSearchRepository(),
      performSafetyNumberChecks = false,
      arbitraryRepository = null,
      searchRepository = SearchRepository(requireContext().getString(R.string.note_to_self)),
      contactSearchPagedDataSourceRepository = ContactSearchPagedDataSourceRepository(requireContext())
    )
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    binding.toolbar.setNavigationOnClickListener {
      requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    binding.toolbar.updateLayoutParams { height = ViewGroup.LayoutParams.WRAP_CONTENT }
    SystemWindowInsetsSetter.attach(binding.toolbar, viewLifecycleOwner, WindowInsetsCompat.Type.statusBars())
    SystemWindowInsetsSetter.attach(binding.recycler, viewLifecycleOwner, WindowInsetsCompat.Type.navigationBars())

    binding.recycler.bind(
      viewModel = contactSearchViewModel,
      fragmentManager = childFragmentManager,
      displayOptions = ContactSearchAdapter.DisplayOptions(
        displayCheckBox = false,
        displaySecondaryInformation = ContactSearchAdapter.DisplaySecondaryInformation.NEVER
      ),
      mapStateToConfiguration = { getConfiguration() }
    )
  }

  private fun getConfiguration(): ContactSearchConfiguration {
    return ContactSearchConfiguration.build {
      addSection(
        ContactSearchConfiguration.Section.Individuals(
          includeHeader = false,
          includeSelfMode = RecipientTable.IncludeSelfMode.Exclude,
          includeLetterHeaders = true,
          transportType = ContactSearchConfiguration.TransportType.PUSH
        )
      )
    }
  }

  class Dialog : WrapperDialogFragment() {
    override fun getWrappedFragment(): Fragment {
      return ViewAllSignalConnectionsFragment()
    }

    companion object {
      fun show(fragmentManager: FragmentManager) {
        Dialog().show(fragmentManager, null)
      }
    }
  }
}
