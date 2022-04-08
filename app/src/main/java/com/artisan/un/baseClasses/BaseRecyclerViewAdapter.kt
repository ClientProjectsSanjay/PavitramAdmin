package com.artisan.un.baseClasses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    private lateinit var layoutInference: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder = run {
        if(!::layoutInference.isInitialized) layoutInference = LayoutInflater.from(parent.context)
        createViewHolder(layoutInference, parent, viewType)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.bindData()

    protected abstract fun createViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): BaseViewHolder
}