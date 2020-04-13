package com.dancing_koala.clairdelune.android

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

class SnapOnScrollListener(
    private val snapHelper: SnapHelper,
    private val behavior: Behavior = Behavior.NOTIFY_ON_SCROLL,
    private val onSnapPositionChangeListener: OnSnapPositionChangeListener? = null
) : RecyclerView.OnScrollListener() {

    enum class Behavior {
        NOTIFY_ON_SCROLL, NOTIFY_ON_STATE_IDLE
    }

    private var snapPosition = RecyclerView.NO_POSITION

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (behavior == Behavior.NOTIFY_ON_SCROLL) {
            maybeNotifySnapPositionChange(recyclerView)
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (behavior == Behavior.NOTIFY_ON_STATE_IDLE && newState == RecyclerView.SCROLL_STATE_IDLE) {
            maybeNotifySnapPositionChange(recyclerView)
        }
    }

    private fun maybeNotifySnapPositionChange(recyclerView: RecyclerView) {
        val newSnapPosition = snapHelper.getSnapPosition(recyclerView)
        val snapPositionChanged = snapPosition != newSnapPosition
        if (snapPositionChanged) {
            onSnapPositionChangeListener?.onSnapPositionChange(newSnapPosition)
            snapPosition = newSnapPosition
        }
    }

    interface OnSnapPositionChangeListener {
        fun onSnapPositionChange(newPosition: Int)
    }
}

fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
    val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
    val snappedView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
    return layoutManager.getPosition(snappedView)
}