package com.pbmt.s_filters.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pbmt.s_filters.repositories.SavedImagesRepository
import com.pbmt.s_filters.utilities.Coroutines
import java.io.File

class SavedImagesViewModel(private val savedImagesRepository: SavedImagesRepository): ViewModel() {
    private val savedImagesDataState = MutableLiveData<SavedImagesDataState>()
    val savedImagesUIState: LiveData<SavedImagesDataState> get() = savedImagesDataState

    fun loadSavedImaages() {
        Coroutines.io {
            kotlin.runCatching {
                emitSavedImageUIState(isLoading = true)
                savedImagesRepository.loadSavedImages()
            }.onSuccess { savedImages->
                if (savedImages.isNullOrEmpty()){
                    emitSavedImageUIState(error = "No image found")
                }else{
                    emitSavedImageUIState(savedImages=savedImages)
                }

            }.onFailure {
                emitSavedImageUIState(error=it.message.toString())
            }
        }
    }


    private fun emitSavedImageUIState(
        isLoading: Boolean=false,
        savedImages:List<Pair<File,Bitmap>>? =null,
        error: String? =null
    ){
        val dataState=SavedImagesDataState(isLoading, savedImages, error)
        savedImagesDataState.postValue(dataState)
    }
    data class  SavedImagesDataState(
        val isLoading:Boolean,
        val savedImages:List<Pair<File,Bitmap>>?,
        val error:String?
    )
}