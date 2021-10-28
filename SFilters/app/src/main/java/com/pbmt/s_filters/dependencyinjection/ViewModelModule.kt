package com.pbmt.s_filters.dependencyinjection

import com.pbmt.s_filters.viewmodels.EditImageViewModel
import com.pbmt.s_filters.viewmodels.SavedImagesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule= module {
    viewModel { EditImageViewModel(editImageRepository = get()) }
    viewModel { SavedImagesViewModel(savedImagesRepository = get()) }
}