package com.artisan.un.utils.recyclerViewDecoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.artisan.un.utils.toPx

class AllSideDecoration(val padding: Int = 8) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = padding.toPx(parent.context).toInt()
        outRect.top = padding.toPx(parent.context).toInt()
        outRect.right = padding.toPx(parent.context).toInt()
        outRect.bottom = padding.toPx(parent.context).toInt()
    }
}