package com.artisan.un.ui.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.artisan.un.apiModel.OrderDetailsProductItem
import com.artisan.un.databinding.ListItemOrderDetailsPurchasedItemBinding

class OrderDetailsPurchasedItemAdapter : RecyclerView.Adapter<OrderDetailsPurchasedItemAdapter.ViewHolder>() {
    private lateinit var mLayoutInflater: LayoutInflater
    private val mDataList = ArrayList<OrderDetailsProductItem>()

    fun addData(data: ArrayList<OrderDetailsProductItem>) {
        mDataList.clear()
        mDataList.addAll(data)
        notifyItemChanged(0, data.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = run {
        if(!::mLayoutInflater.isInitialized) mLayoutInflater = LayoutInflater.from(parent.context)
        ViewHolder(ListItemOrderDetailsPurchasedItemBinding.inflate(mLayoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.initView(mDataList[holder.adapterPosition])
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    class ViewHolder(private val viewBinding: ListItemOrderDetailsPurchasedItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {

        fun initView(data: OrderDetailsProductItem) {
            viewBinding.data = data
            viewBinding.isColored = adapterPosition%2 == 0
        }
    }
}