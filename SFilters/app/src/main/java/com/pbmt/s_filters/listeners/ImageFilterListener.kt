package com.pbmt.s_filters.listeners

import com.pbmt.s_filters.data.ImageFilters

interface ImageFilterListener {
    fun onFilterSelected(imageFilter: ImageFilters)
}