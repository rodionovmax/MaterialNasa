package com.rodionovmax.materialnasa.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rodionovmax.materialnasa.BuildConfig
import com.rodionovmax.materialnasa.data.model.MarsPhoto
import com.rodionovmax.materialnasa.data.network.NasaApiService
import com.rodionovmax.materialnasa.data.network.model.MarsPhotoDto
import com.rodionovmax.materialnasa.data.network.model.MarsResultsDto
import com.rodionovmax.materialnasa.data.repo_impl.ROVER
import com.rodionovmax.materialnasa.data.repo_impl.RemoteRepoImpl
import com.rodionovmax.materialnasa.utils.asDomainMarsPhotos
import retrofit2.HttpException
import java.lang.Exception

class MarsPhotoPagingSource(
    private val nasaApiService: NasaApiService,
    val camera: String?,
    val earthDate: String
) : PagingSource<Int, MarsPhoto>() {

    override fun getRefreshKey(state: PagingState<Int, MarsPhoto>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
        return anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MarsPhoto> {
        try {
            val pageNumber = params.key ?: INITIAL_PAGE_NUMBER
            val pageSize = params.loadSize.coerceAtMost(MAX_PAGE_SIZE)
            val response: MarsResultsDto = if (camera == null) {
                nasaApiService.getAllRoverPhotos(BuildConfig.NASA_API_KEY, ROVER, earthDate)
            } else {
                nasaApiService.getRoverPhotosForCamera(BuildConfig.NASA_API_KEY, ROVER, camera, earthDate)
            }

            val photos: List<MarsPhoto> = asDomainMarsPhotos(response)
            val nextPageNumber = if (photos.isEmpty()) null else pageNumber + 1
            val prevPageNumber = if (pageNumber > 1) pageNumber - 1 else null
            return LoadResult.Page(photos, prevPageNumber, nextPageNumber)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    companion object {
        const val INITIAL_PAGE_NUMBER = 1
        const val MAX_PAGE_SIZE = 50
    }
}