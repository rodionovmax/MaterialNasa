package com.rodionovmax.materialnasa.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.rodionovmax.materialnasa.R
import com.rodionovmax.materialnasa.databinding.SearchLoadStateFooterBinding

class SearchLoadStateViewHolder(
    private val binding: SearchLoadStateFooterBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root){
    init {
        binding.buttonRetry.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.textViewError.text = loadState.error.localizedMessage
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.buttonRetry.isVisible = loadState is LoadState.Error
        binding.textViewError.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): SearchLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.search_load_state_footer, parent, false)
            val binding = SearchLoadStateFooterBinding.bind(view)
            return SearchLoadStateViewHolder(binding, retry)
        }
    }
}