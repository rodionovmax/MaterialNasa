package com.rodionovmax.materialnasa.ui.search

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.rodionovmax.materialnasa.databinding.SearchLoadStateFooterBinding

class SearchLoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<SearchLoadStateViewHolder>(){
    override fun onBindViewHolder(holder: SearchLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): SearchLoadStateViewHolder {
        return SearchLoadStateViewHolder.create(parent, retry)
    }
}