package com.rodionovmax.materialnasa.data.repo_impl

import androidx.paging.*
import com.rodionovmax.materialnasa.BuildConfig
import com.rodionovmax.materialnasa.data.MarsPhotoPagingSource
import com.rodionovmax.materialnasa.data.Result
import com.rodionovmax.materialnasa.data.getResult
import com.rodionovmax.materialnasa.data.model.MarsPhoto
import com.rodionovmax.materialnasa.data.network.model.PodDto
import com.rodionovmax.materialnasa.data.network.NasaApiService
import com.rodionovmax.materialnasa.data.model.Pod
import com.rodionovmax.materialnasa.data.repo.RemoteRepo
import com.rodionovmax.materialnasa.utils.asDomainMarsPhotos
import com.rodionovmax.materialnasa.utils.asDomainPod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://api.nasa.gov/"
const val ROVER = "curiosity"

class RemoteRepoImpl : RemoteRepo {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api: NasaApiService = retrofit.create(NasaApiService::class.java)
    private val apiKey = BuildConfig.NASA_API_KEY

    override fun getPictureOfTheDay(
        date: String,
        onSuccess: (Pod) -> Unit,
        onError: ((Throwable) -> Unit)?
    ) {
        api.getPod(apiKey, date).enqueue(object : Callback<PodDto> {
            override fun onResponse(call: Call<PodDto>, response: Response<PodDto>) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    onSuccess.invoke(body.asDomainPod())
                } else {
                    onError?.invoke(IllegalStateException("Data error"))
                }
            }

            override fun onFailure(call: Call<PodDto>, t: Throwable) {
                onError?.invoke(t)
            }

        })
    }

    override suspend fun getPhotosForCamera(
        camera: String,
        earthDate: String
    ): Result<List<MarsPhoto>> = getResult {
        withContext(Dispatchers.IO) {
            val response = api.getRoverPhotosForCamera(ROVER, apiKey, camera, earthDate)
            asDomainMarsPhotos(response)
        }
    }

    override suspend fun getAllPhotos(earthDate: String): Result<List<MarsPhoto>> = getResult {
        withContext(Dispatchers.IO) {
            val response = api.getAllRoverPhotos(ROVER, apiKey, earthDate)
            asDomainMarsPhotos(response)
        }
    }

    override suspend fun getMarsPhotosStream(camera: String, earthDate: String): Result<Flow<PagingData<MarsPhoto>>> = getResult {
        return@getResult Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MarsPhotoPagingSource(api, camera, earthDate) }
        ).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 50
    }

}

