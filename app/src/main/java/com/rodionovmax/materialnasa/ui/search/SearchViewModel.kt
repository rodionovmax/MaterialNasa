package com.rodionovmax.materialnasa.ui.search

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rodionovmax.materialnasa.data.model.NewsArticle
import com.rodionovmax.materialnasa.data.model.SearchResult
import com.rodionovmax.materialnasa.data.repo.RemoteRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlin.coroutines.coroutineContext

class SearchViewModel(
    private val repo: RemoteRepo
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val results: StateFlow<PagingData<SearchResult>> = query
        .flatMapLatest { queryString ->
            repo.getSearchResultsStream(queryString).distinctUntilChanged().cachedIn(viewModelScope)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

    val news: StateFlow<PagingData<NewsArticle>> = query
        .flatMapLatest { queryString ->
            repo.getEverything(queryString)
        }.stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

    fun setQuery(query: String) {
        _query.tryEmit(query)
    }

    fun search(query: String) {
        _query.value = query
    }

    companion object {
        private const val DEFAULT_QUERY = "apollo 11"
    }
}