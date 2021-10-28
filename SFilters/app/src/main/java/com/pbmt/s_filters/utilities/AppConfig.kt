package com.pbmt.s_filters.utilities

import android.app.Application
import com.pbmt.s_filters.dependencyinjection.repositoryModule
import com.pbmt.s_filters.dependencyinjection.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@Suppress("unused")
class AppConfig: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AppConfig)
            modules(listOf(repositoryModule, viewModelModule))
        }
    }
}