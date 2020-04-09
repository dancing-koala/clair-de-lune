package com.dancing_koala.lunedejour.ui

import android.os.Bundle
import android.os.Handler
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import coil.api.load
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler
import com.dancing_koala.lunedejour.R
import com.dancing_koala.lunedejour.data.MoonImages
import kotlinx.android.synthetic.main.activity_home.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class HomeActivity : AppCompatActivity() {
    companion object {
        private const val AUTO_HIDE = true
        private const val AUTO_HIDE_DELAY_MILLIS = 3000
        private const val UI_ANIMATION_DELAY = 300
    }

    private val hideHandler = Handler()

    private val hidePart2Runnable = Runnable {
        homeMainImageView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        homeBottomToolbar.visibility = View.VISIBLE
    }

    private var isFullScreen: Boolean = false
    private val hideRunnable = Runnable { hide() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val imageMatrixTouchHandler = ImageMatrixTouchHandler(this)
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                toggle()
                return true
            }
        })

        homeMainImageView.setOnTouchListener { view, event ->
            gestureDetector.onTouchEvent(event)
            imageMatrixTouchHandler.onTouch(view, event)

            true
        }

        homeMainImageView.load(MoonImages.urls.random())
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        isFullScreen = true
        delayedHide(100)
    }

    private fun toggle() = if (isFullScreen) hide() else show()

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        homeBottomToolbar.visibility = View.GONE
        isFullScreen = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        homeMainImageView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        isFullScreen = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }
}
