package com.rodionovmax.materialnasa.domain.impl

import com.rodionovmax.materialnasa.BuildConfig
import com.rodionovmax.materialnasa.data.network.model.PodDto
import com.rodionovmax.materialnasa.data.network.NasaApiService
import com.rodionovmax.materialnasa.domain.model.Pod
import com.rodionovmax.materialnasa.domain.repo.RemoteRepo
import com.rodionovmax.materialnasa.utils.asDomainPod
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://api.nasa.gov/"

class RemoteRepoImpl : RemoteRepo {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api: NasaApiService = retrofit.create(NasaApiService::class.java)
    private val apiKey = BuildConfig.NASA_API_KEY

    override fun getPictureOfTheDay(date: String, onSuccess: (Pod) -> Unit, onError: ((Throwable) -> Unit)?) {
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


}

