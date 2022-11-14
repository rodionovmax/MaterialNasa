package com.rodionovmax.materialnasa.ui.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.paging.LoadState
import com.rodionovmax.materialnasa.app
import com.rodionovmax.materialnasa.databinding.FragmentSearchBinding
import com.rodionovmax.materialnasa.utils.hideSoftKeyboard
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by lazy { SearchViewModel(app.remoteRepo) }
    private val adapter by lazy { SearchAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.resultsRecyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
            header = SearchLoadStateAdapter { adapter.retry() },
            footer = SearchLoadStateAdapter { adapter.retry() }
        )

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.news.collectLatest(adapter::submitData)
                viewModel.results.collectLatest(adapter::submitData)
            }
        }

        /*binding.search.doAfterTextChanged { text ->
            viewModel.setQuery(text.toString())
        }*/

        adapter.addLoadStateListener { state ->
            binding.resultsRecyclerView.isVisible = state.refresh != LoadState.Loading
            binding.progressBar.isVisible = state.refresh == LoadState.Loading
        }

        viewModel.query
            .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
            .onEach(::updateSearchQuery)
            .launchIn(lifecycleScope)

        binding.search.setOnKeyListener { v, keyCode, event ->
            if (event != null) {
                if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    viewModel.search(binding.search.text.toString())
                    hideSoftKeyboard()
                }
                true
            } else {
                false
            }
        }
    }

    private fun updateSearchQuery(searchQuery: String) {
        if ((binding.search.text?.toString() ?: "") != searchQuery) {
            binding.search.setText(searchQuery)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.search(binding.search.text.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}