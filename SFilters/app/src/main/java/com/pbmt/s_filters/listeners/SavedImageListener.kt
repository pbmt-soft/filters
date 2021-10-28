package com.pbmt.s_filters.listeners

import java.io.File

interface SavedImageListener {
    fun onImageClicked(file: File)
}