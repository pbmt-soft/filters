package com.pbmt.s_filters.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pbmt.s_filters.R
import com.pbmt.s_filters.data.ImageFilters
import com.pbmt.s_filters.databinding.ItemContainerFilterBinding
import com.pbmt.s_filters.listeners.ImageFilterListener

class ImageFiltersAdapter(
    private val imageFilter:List<ImageFilters>,
    private val imageFilterListener: ImageFilterListener
)
    :RecyclerView.Adapter<ImageFiltersAdapter.ImageFilterViewHolder>() {

    private var selectedFilterPosition=0
    private var previousSelectedPosition=0

    inner class ImageFilterViewHolder(val binding: ItemContainerFilterBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageFilterViewHolder {
        val binding =
            ItemContainerFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageFilterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageFilterViewHolder, position: Int) {
        with(holder) {
            with(imageFilter[position]) {
                binding.imageFilterPreview.setImageBitmap(filterPreview)
                binding.textFilterName.text = name
                binding.root.setOnClickListener {
                    if(position != selectedFilterPosition){
                        imageFilterListener.onFilterSelected(this)
                        previousSelectedPosition=selectedFilterPosition
                        selectedFilterPosition=position
                        with(this@ImageFiltersAdapter){
                            notifyItemChanged(previousSelectedPosition,Unit)
                            notifyItemChanged(selectedFilterPosition,Unit)
                        }
                    }
                }
            }
            binding.textFilterName.setTextColor(
                ContextCompat.getColor(
                    binding.textFilterName.context,
                    if (selectedFilterPosition==position)
                        R.color.primaryDark
                    else
                        R.color.primaryText
                )
            )
        }
    }

    override fun getItemCount() = imageFilter.size

}