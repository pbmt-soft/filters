package com.pbmt.s_filters.viewmodels

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pbmt.s_filters.data.ImageFilters
import com.pbmt.s_filters.repositories.EditImageRepository
import com.pbmt.s_filters.utilities.Coroutines

class EditImageViewModel(private val editImageRepository: EditImageRepository) :ViewModel(){

    //region:: Prepare image preview

    private val imagePreviewDataState=MutableLiveData<ImagePreviewDataState>()
    val imagePreviewUiState: LiveData<ImagePreviewDataState>get() = imagePreviewDataState

    fun prepareImagePreview(imageUri:Uri){
        Coroutines.io{
            kotlin.runCatching {
                emitImagePreviewUIState(isLoading = true)
                editImageRepository.prepareImagePreview(imageUri)
            }.onSuccess { bitmap->
                if (bitmap !=null){
                    emitImagePreviewUIState(bitmap = bitmap)
                }else{
                    emitImagePreviewUIState(error = "Unable to prepare image preview")
                }
            }.onFailure {
                emitImagePreviewUIState(error = it.message.toString())
            }
        }
    }

    private fun emitImagePreviewUIState(
        isLoading: Boolean=false,
        bitmap: Bitmap? =null,
        error: String? =null
    ){
        val dataState=ImagePreviewDataState(isLoading, bitmap, error)
        imagePreviewDataState.postValue(dataState)
    }

    data class  ImagePreviewDataState(
        val isLoading:Boolean,
        val bitmap: Bitmap?,
        val error:String?

    )

    //endregion

    //region:: Load image filters

    private val imageFiltersDataState=MutableLiveData<ImageFiltersDataState>()
    val imageFiltersUiState: LiveData<ImageFiltersDataState> get() = imageFiltersDataState

    fun loadImageFilters(originalImage: Bitmap){
        Coroutines.io {
            runCatching {
                emitImageFiltersUIState(isLoading = true)
                editImageRepository.getImageFilters(getPreviewImage(originalImage))
            }.onSuccess { imageFilters->
                emitImageFiltersUIState(imageFilters = imageFilters)
            }.onFailure {
                emitImageFiltersUIState(error = it.message.toString())
            }
        }
    }

    private fun getPreviewImage(originalImage: Bitmap):Bitmap{
        return kotlin.runCatching {
            val previewWidth=150
            val previewHeight= originalImage.height* previewWidth/originalImage.width
            Bitmap.createScaledBitmap(originalImage,previewWidth,previewHeight,false)
        }.getOrDefault(originalImage)
    }

    private fun emitImageFiltersUIState(
        isLoading: Boolean= false,
        imageFilters: List<ImageFilters>? = null,
        error: String? = null
    ){
        val dataState=ImageFiltersDataState(isLoading, imageFilters, error)
        imageFiltersDataState.postValue(dataState)
    }

    data class  ImageFiltersDataState(
        val isLoading: Boolean,
        val imageFilters: List<ImageFilters>?,
        val error: String?
    )

    //endregion

    //region::save filtered image
    private val saveFilteredImageDataState=MutableLiveData<SaveFilteredImageDataState>()
    val saveFilteredImageUIState:LiveData<SaveFilteredImageDataState> get() = saveFilteredImageDataState

    fun saveFilteredImage(filteredBitmap:Bitmap){
        Coroutines.io {
            kotlin.runCatching {
                emitSaveFilteredImageUIState(isLoading = true)
                editImageRepository.saveFilteredImage(filteredBitmap)
            }.onSuccess { savedImageUri->
                emitSaveFilteredImageUIState(uri = savedImageUri)
            }.onFailure {
                emitSaveFilteredImageUIState(error = it.message.toString())
            }
        }
    }

    private fun emitSaveFilteredImageUIState(
            isLoading: Boolean=false,
            uri: Uri?=null,
            error: String? = null
    ){
        val dataState=SaveFilteredImageDataState(isLoading, uri, error)
        saveFilteredImageDataState.postValue(dataState)
    }

    data class SaveFilteredImageDataState(
            val isLoading: Boolean,
            val uri:Uri?,
            val error: String?

    )
    //endregion
}