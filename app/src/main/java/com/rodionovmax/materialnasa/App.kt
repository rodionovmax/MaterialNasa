package com.rodionovmax.materialnasa

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import com.rodionovmax.materialnasa.data.RemoteRepoImpl
import com.rodionovmax.materialnasa.domain.repo.RemoteRepo

class App : Application() {
    val remoteRepo: RemoteRepo by lazy { RemoteRepoImpl() }
}

val Context.app: App get() = applicationContext as App
val Fragment.app: App get() = requireContext().applicationContext as App