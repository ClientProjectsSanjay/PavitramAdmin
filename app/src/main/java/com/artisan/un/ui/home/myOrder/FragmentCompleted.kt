package com.artisan.un.ui.home.myOrder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.artisan.un.databinding.ViewRecyclerviewBinding

class FragmentCompleted : Fragment() {
    private lateinit var mDataBinding: ViewRecyclerviewBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mDataBinding = ViewRecyclerviewBinding.inflate(inflater, container, false)
        return mDataBinding.root
    }
}