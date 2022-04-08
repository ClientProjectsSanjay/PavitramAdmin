package com.artisan.un.ui.product.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.artisan.un.baseClasses.BaseRecyclerViewAdapter
import com.artisan.un.baseClasses.BaseViewHolder
import com.artisan.un.databinding.ViewProductRoundCornerImageBinding
import com.artisan.un.utils.getVideoThumbnail
import com.artisan.un.utils.redirectTo
import kotlin.collections.ArrayList

class PreviewProductImageAdapter : BaseRecyclerViewAdapter() {
    private var data: ArrayList<String>? = null
    private var thumbnailUrl: String = ""
    private var videoUrl: String? = null

    fun setData(data: ArrayList<String>?, videoUrl: String?) {
        this.videoUrl = videoUrl
        this.data = data

        this.videoUrl?.let {
            thumbnailUrl = getVideoThumbnail(it)
            this.data?.add(thumbnailUrl)
        }

        notifyDataSetChanged()
    }

    override fun createViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): BaseViewHolder = run {
        ProductImageListItemHolder(ViewProductRoundCornerImageBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount() = this.data?.size ?: 0

    inner class ProductImageListItemHolder(val viewDataBinding : ViewProductRoundCornerImageBinding) : BaseViewHolder(viewDataBinding) {
        override fun bindData() {
            data?.let {
                viewDataBinding.image = it[adapterPosition]
                viewDataBinding.youtubeImg.visibility = if(it[adapterPosition] == thumbnailUrl) View.VISIBLE else View.GONE

                viewDataBinding.root.setOnClickListener { _ ->
                    if(it[adapterPosition] == thumbnailUrl) {
                        viewDataBinding.root.context.redirectTo(videoUrl ?: "")
                    } else if(adapterPosition > 0) {
                        val temp = it[adapterPosition]
                        it[adapterPosition] = it[0]
                        it[0] = temp

                        notifyItemChanged(0)
                        notifyItemChanged(adapterPosition)
                    }
                }
            }
        }
    }
}