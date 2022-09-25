package com.rodionovmax.materialnasa

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import com.rodionovmax.materialnasa.data.local.LocalDatabase
import com.rodionovmax.materialnasa.data.repo_impl.LocalRepoImpl
import com.rodionovmax.materialnasa.data.repo.LocalRepo
import com.rodionovmax.materialnasa.data.repo.RemoteRepo
import com.rodionovmax.materialnasa.data.repo_impl.RemoteRepoImpl

class App : Application() {
    val remoteRepo: RemoteRepo by lazy { RemoteRepoImpl() }
    val localRepo: LocalRepo by lazy { LocalRepoImpl(getDb().localDao) }
    private lateinit var appInstance: App

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

    private fun getDb() = LocalDatabase.getInstance(appInstance.applicationContext)
}

val Context.app: App get() = applicationContext as App
val Fragment.app: App get() = requireContext().applicationContext as App