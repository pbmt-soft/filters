package com.pbmt.s_filters.activities.editimages

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.pbmt.s_filters.activities.filteredimage.FilteredImageActivity
import com.pbmt.s_filters.activities.main.MainActivity
import com.pbmt.s_filters.adapters.ImageFiltersAdapter
import com.pbmt.s_filters.data.ImageFilters
import com.pbmt.s_filters.databinding.ActivityEditImageBinding
import com.pbmt.s_filters.listeners.ImageFilterListener
import com.pbmt.s_filters.utilities.displayToast
import com.pbmt.s_filters.utilities.show
import com.pbmt.s_filters.viewmodels.EditImageViewModel
import jp.co.cyberagent.android.gpuimage.GPUImage
import org.koin.androidx.viewmodel.ext.android.viewModel


class EditImageActivity : AppCompatActivity() , ImageFilterListener{

    companion object{
        const val  KEY_FILTERED_IMAGE_URI="filteredImageUri"
    }

    private lateinit var  binding: ActivityEditImageBinding

    private val viewModel:EditImageViewModel by viewModel()

    private lateinit var gpuImage:GPUImage

    private  lateinit var originalBitmap:Bitmap
    private val filteredBitmap=MutableLiveData<Bitmap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
        setupObservers()
        prepareImagePreview()
    }

    private fun setupObservers(){
        viewModel.imagePreviewUiState.observe(this,{
            val dataState= it ?: return@observe
            binding.previewProgressbar.visibility=
                if (dataState.isLoading) View.VISIBLE else View.GONE
            dataState.bitmap?.let{bitmap->
                //for  the first time
                originalBitmap=bitmap
                filteredBitmap.value=bitmap

                with(originalBitmap){
                    gpuImage.setImage(this)
                    binding.imagePreview.show()
                    viewModel.loadImageFilters(this)
                }

            } ?: kotlin.run {
                dataState.error?.let{error->
                    displayToast(error)
                }
            }
        })
        viewModel.imageFiltersUiState.observe(this,{
            val imageFilterDataState= it ?: return@observe
            binding.imageFilterProgressbar.visibility=
                if (imageFilterDataState.isLoading) View.VISIBLE else View.GONE
            imageFilterDataState.imageFilters?.let{imageFilters->
               ImageFiltersAdapter(imageFilters,this).also { adapter->
                   binding.filtersRecyclerView.adapter=adapter
               }
            } ?: kotlin.run {
                imageFilterDataState.error?.let{error->
                    displayToast(error)
                }
            }
        })
        filteredBitmap.observe(this,{bitmap->
            binding.imagePreview.setImageBitmap(bitmap)
        })

        viewModel.saveFilteredImageUIState.observe(this, {
            val saveFilteredImageDataState=it ?:return@observe
            if (saveFilteredImageDataState.isLoading){
                binding.imageSave.visibility=View.GONE
                binding.savingProgressbar.visibility=View.VISIBLE
            }else{
                binding.imageSave.visibility=View.VISIBLE
                binding.savingProgressbar.visibility=View.GONE
            }
            saveFilteredImageDataState.uri?.let { saveImageUri->
                Intent(applicationContext,
                FilteredImageActivity::class.java).also { filteredImageIntent->
                    filteredImageIntent.putExtra(KEY_FILTERED_IMAGE_URI,saveImageUri)
                    startActivity(filteredImageIntent)
                }
            } ?: kotlin.run {
                saveFilteredImageDataState.error?.let { error->
                    displayToast(error)
                }
            }
        })
    }

    private fun prepareImagePreview(){
        gpuImage= GPUImage(applicationContext)
        intent.getParcelableExtra<Uri>(MainActivity.KEY_IMAGE_URI)?.let { imageUri ->
            viewModel.prepareImagePreview(imageUri)
        }
    }

    private fun displayImagePreview(){
        intent.getParcelableExtra<Uri>(MainActivity.KEY_IMAGE_URI)?.let{ imageUri->
            val inputStream=contentResolver.openInputStream(imageUri)
            val bitmap=BitmapFactory.decodeStream(inputStream)
            binding.imagePreview.setImageBitmap(bitmap)
            binding.imagePreview.visibility=View.VISIBLE

        }
    }

    private fun setListeners(){
        binding.imageBack.setOnClickListener {
            onBackPressed()
        }
        binding.imageSave.setOnClickListener {
            filteredBitmap.value?.let { bitmap->
                viewModel.saveFilteredImage(bitmap)
            }
        }
        binding.imagePreview.setOnLongClickListener {
            binding.imagePreview.setImageBitmap(originalBitmap)
            return@setOnLongClickListener false
        }
        binding.imagePreview.setOnClickListener {
            binding.imagePreview.setImageBitmap(filteredBitmap.value)
        }
    }

    override fun onFilterSelected(imageFilter: ImageFilters) {
        with(imageFilter){
            with(gpuImage){
                setFilter(filter)
                filteredBitmap.value= bitmapWithFilterApplied
            }
        }
    }
}