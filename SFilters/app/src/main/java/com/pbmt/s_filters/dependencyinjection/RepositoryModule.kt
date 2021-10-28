package com.pbmt.s_filters.dependencyinjection

import com.pbmt.s_filters.repositories.EditImageRepository
import com.pbmt.s_filters.repositories.EditImageRepositoryImpl
import com.pbmt.s_filters.repositories.SavedImagesRepository
import com.pbmt.s_filters.repositories.SavedImagesRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule= module {
    factory<EditImageRepository> {EditImageRepositoryImpl(androidContext())  }
    factory<SavedImagesRepository> {SavedImagesRepositoryImpl(androidContext())}
}