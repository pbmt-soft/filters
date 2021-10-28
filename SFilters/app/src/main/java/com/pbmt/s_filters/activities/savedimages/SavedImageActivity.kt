package com.pbmt.s_filters.activities.savedimages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.FileProvider
import com.pbmt.s_filters.activities.editimages.EditImageActivity
import com.pbmt.s_filters.activities.filteredimage.FilteredImageActivity
import com.pbmt.s_filters.adapters.SavedImagesAdapter
import com.pbmt.s_filters.databinding.ActivitySavedImageBinding
import com.pbmt.s_filters.listeners.SavedImageListener
import com.pbmt.s_filters.utilities.displayToast
import com.pbmt.s_filters.viewmodels.SavedImagesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class SavedImageActivity : AppCompatActivity() , SavedImageListener{

    private lateinit var  binding: ActivitySavedImageBinding
    private val viewModel:SavedImagesViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySavedImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupObserver()
        setListeners()
        viewModel.loadSavedImaages()
    }

    private fun setupObserver(){
        viewModel.savedImagesUIState.observe(this,{
            val savedImagesDataState=it ?: return@observe
            binding.savedImageProgressbar.visibility=
                if (savedImagesDataState.isLoading) View.VISIBLE else  View.GONE
            savedImagesDataState.savedImages?.let { savedImages->
                SavedImagesAdapter(savedImages,this).also { adapter->
                    with(binding.savedImageRecyclerView){
                        this.adapter=adapter
                        visibility=View.VISIBLE
                    }
                }
            } ?: kotlin.run {
                savedImagesDataState.error?.let { error->
                    displayToast(error)
                }
            }
        })
    }

    private fun setListeners(){
        binding.imageBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onImageClicked(file: File) {
        val fileUri=FileProvider.getUriForFile(
            applicationContext,
            "${packageName}.provider",
            file
        )
        Intent(
            applicationContext,
            FilteredImageActivity::class.java
        ).also { filteredImageIntent->
            filteredImageIntent.putExtra(EditImageActivity.KEY_FILTERED_IMAGE_URI,fileUri)
            startActivity(filteredImageIntent)
        }
    }
}