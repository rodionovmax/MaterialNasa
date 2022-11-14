package com.rodionovmax.materialnasa.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rodionovmax.materialnasa.data.model.SearchResult
import com.rodionovmax.materialnasa.data.network.NasaApiService
import com.rodionovmax.materialnasa.data.network.model.DataDto
import com.rodionovmax.materialnasa.data.network.model.LinkDto
import com.rodionovmax.materialnasa.data.repo_impl.RemoteRepoImpl
import com.rodionovmax.materialnasa.utils.asDomainSearchResults
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_KEY = 0
private const val GITHUB_STARTING_PAGE_INDEX = 1

// switch to SearchRemoteMediator to save results to the database
class NasaSearchPagingSource(
    private val service: NasaApiService,
    private val query: String
): PagingSource<Int, SearchResult>() {
    override val keyReuseSupported: Boolean = true

    override fun getRefreshKey(state: PagingState<Int, SearchResult>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
        return anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchResult> {
        val position = params.key ?: GITHUB_STARTING_PAGE_INDEX
//        val apiQuery = query + IN_QUALIFIER
        return try {
//            val response = service.searchResults(query, position, params.loadSize)
            val response = service.searchResults(query)
            val items = response.collection.items
            Log.e("myItems", "$items")

            val results = asDomainSearchResults(items)
            Log.e("myResults", "$results")

            val nextKey = if (results.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (params.loadSize / RemoteRepoImpl.NETWORK_PAGE_SIZE)
            }
            LoadResult.Page(
                data = results,
                prevKey = if (position == GITHUB_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )

        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}
