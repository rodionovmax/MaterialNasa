package com.rodionovmax.materialnasa

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import com.rodionovmax.materialnasa.data.repo.NasaRepository
import com.rodionovmax.materialnasa.data.local.LocalDatabase
import com.rodionovmax.materialnasa.data.network.NasaService
import com.rodionovmax.materialnasa.data.repo_impl.LocalRepoImpl
import com.rodionovmax.materialnasa.data.repo.LocalRepo
import com.rodionovmax.materialnasa.data.repo.RemoteRepo
import com.rodionovmax.materialnasa.data.repo_impl.RemoteRepoImpl
import com.rodionovmax.materialnasa.domain.FetchMarsPhotosUseCase

class App : Application() {
    val remoteRepo: RemoteRepo by lazy { RemoteRepoImpl() }
    val localRepo: LocalRepo by lazy { LocalRepoImpl(getDb().localDao, getDb().roverGalleryDao) }
    val nasaRepo: NasaRepository by lazy { NasaRepository(NasaService.create(), getDb()) }
    val fetchMarsPhotosUseCase: FetchMarsPhotosUseCase by lazy { FetchMarsPhotosUseCase(remoteRepo, localRepo) }
    private lateinit var appInstance: App

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

    private fun getDb() = LocalDatabase.getInstance(appInstance.applicationContext)
}

val Context.app: App get() = applicationContext as App
val Fragment.app: App get() = requireContext().applicationContext as App