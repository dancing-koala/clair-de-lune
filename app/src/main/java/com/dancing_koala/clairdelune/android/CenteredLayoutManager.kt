package com.dancing_koala.clairdelune.android

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import kotlin.math.roundToInt

class CenteredLayoutManager(
    context: Context,
    private val parentWidth: Int,
    private val itemWidth: Int
) : LinearLayoutManager(context, HORIZONTAL, true) {

    override fun getPaddingLeft(): Int = (parentWidth / 2f - itemWidth / 2f).roundToInt()

    override fun getPaddingRight(): Int = paddingLeft
}