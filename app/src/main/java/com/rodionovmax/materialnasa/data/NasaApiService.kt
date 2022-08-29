package com.rodionovmax.materialnasa.data

import com.rodionovmax.materialnasa.data.model.PodDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApiService {
    @GET("planetary/apod")
    fun getPod(
        @Query("api_key") apiKey: String,
        @Query("date") date: String
    ) : Call<PodDto>
}