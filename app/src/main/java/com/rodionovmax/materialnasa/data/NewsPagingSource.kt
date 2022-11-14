package com.rodionovmax.materialnasa.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rodionovmax.materialnasa.BuildConfig
import com.rodionovmax.materialnasa.data.model.NewsArticle
import com.rodionovmax.materialnasa.data.network.NewsApiService
import com.rodionovmax.materialnasa.data.repo_impl.RemoteRepoImpl
import com.rodionovmax.materialnasa.utils.toArticle
import retrofit2.HttpException
import java.io.IOException

private const val NEWS_STARTING_PAGE_INDEX = 1

class NewsPagingSource(
    private val newsApi: NewsApiService,
    private val query: String
) : PagingSource<Int, NewsArticle>(){

    override val keyReuseSupported: Boolean = true

    override fun getRefreshKey(state: PagingState<Int, NewsArticle>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsArticle> {
        val position = params.key ?: NEWS_STARTING_PAGE_INDEX
        return try {
            val response = newsApi.getEverything(query, position, params.loadSize)
            val news = response.articles.map { it.toArticle() }
            val nextKey = if (news.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (params.loadSize / RemoteRepoImpl.NETWORK_PAGE_SIZE)
            }
            LoadResult.Page(
                data = news,
                prevKey = if (position == NEWS_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            Log.d("myTag", "$exception")
            return LoadResult.Error(exception)
        }
    }
}