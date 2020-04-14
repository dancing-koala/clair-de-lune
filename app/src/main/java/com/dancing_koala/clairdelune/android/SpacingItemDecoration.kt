package com.dancing_koala.clairdelune.android

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.apply {
            left = spaceHeight
            right = spaceHeight
//            top = spaceHeight
//            bottom = spaceHeight
        }
    }
}