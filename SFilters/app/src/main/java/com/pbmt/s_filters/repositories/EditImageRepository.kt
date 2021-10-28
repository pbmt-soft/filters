package com.pbmt.s_filters.repositories

import android.graphics.Bitmap
import android.net.Uri
import com.pbmt.s_filters.data.ImageFilters

interface EditImageRepository {
    suspend fun  prepareImagePreview(imageUri: Uri):Bitmap?
    suspend fun  getImageFilters(image: Bitmap):List<ImageFilters>
    suspend fun  saveFilteredImage(filteredBitmap: Bitmap): Uri?
}