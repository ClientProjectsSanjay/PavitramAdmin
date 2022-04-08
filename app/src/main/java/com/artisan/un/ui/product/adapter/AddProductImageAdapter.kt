package com.artisan.un.ui.product.adapter

import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseRecyclerViewAdapter
import com.artisan.un.baseClasses.BaseViewHolder
import com.artisan.un.databinding.ViewProductAddListItemBinding
import com.artisan.un.utils.ApplicationData
import com.artisan.un.utils.ProductMediaType
import com.artisan.un.utils.getVideoThumbnail

class AddProductImageAdapter(var onPickImage: (Int) -> Unit) : BaseRecyclerViewAdapter() {
    private val dataMap = ArrayList<Pair<ProductMediaType, String?>>()

    fun addData(key: ProductMediaType, data: String?, position: Int = -1): Int = run {
        if(position > -1 && dataMap.size > position) {
            dataMap.add(position, Pair(key, data))
            dataMap.removeAt(position + 1)
            position
        } else {
            dataMap.add(Pair(key, data))
            dataMap.size - 1
        }
    }

    fun addImage(data: String?) {
        dataMap.add(Pair(ProductMediaType.IMAGE, data))
    }

    fun resetData() {
        dataMap.clear()
    }

    fun isImageSelected() = run {
        dataMap.any { it.first == ProductMediaType.IMAGE }
    }

    fun getSelectedImages() = run {
        dataMap.filter { it.first == ProductMediaType.IMAGE }
    }

    override fun createViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): BaseViewHolder = run {
        ProductImageListItemHolder(ViewProductAddListItemBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount() = 5

    inner class ProductImageListItemHolder(val viewDataBinding: ViewProductAddListItemBinding) : BaseViewHolder(viewDataBinding) {
        override fun bindData() {
            if(dataMap.size > adapterPosition) {
                viewDataBinding.closeBtn.visibility = View.VISIBLE
                viewDataBinding.isVideo = dataMap[adapterPosition].first == ProductMediaType.VIDEO

                if(dataMap[adapterPosition].first == ProductMediaType.VIDEO) {
                    viewDataBinding.productImage = getVideoThumbnail(dataMap[adapterPosition].second)
                } else {
                    if(Patterns.WEB_URL.matcher(dataMap[adapterPosition].second ?: "").matches()) viewDataBinding.productImage = dataMap[adapterPosition].second
                    else viewDataBinding.image.setImageURI(dataMap[adapterPosition].second?.toUri())
                }
            } else {
                viewDataBinding.image.setImageDrawable(ContextCompat.getDrawable(viewDataBinding.root.context, R.drawable.ic_baseline_add))
                viewDataBinding.closeBtn.visibility = View.GONE
                viewDataBinding.isVideo = false
            }

            viewDataBinding.root.setOnClickListener { onPickImage(adapterPosition) }

            viewDataBinding.closeBtn.setOnClickListener {
                if(dataMap[adapterPosition].first == ProductMediaType.VIDEO) {
                    dataMap.removeAt(adapterPosition)
                    ApplicationData.productBundle?.youtubeUrl = null
                } else {
                    dataMap.removeAt(adapterPosition)

                    val selectedImages = getSelectedImages()
                    ApplicationData.productBundle?.run {
                        image1 = if(selectedImages.isNotEmpty()) selectedImages[0].second else null
                        image2 = if(selectedImages.size > 1) selectedImages[1].second else null
                        image3 = if(selectedImages.size > 2) selectedImages[2].second else null
                        image4 = if(selectedImages.size > 3) selectedImages[3].second else null
                        image5 = if(selectedImages.size > 4) selectedImages[4].second else null
                    }
                }

                notifyDataSetChanged()
            }
        }
    }
}