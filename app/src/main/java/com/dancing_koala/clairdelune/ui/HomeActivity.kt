package com.dancing_koala.clairdelune.ui

import android.os.Bundle
import android.os.Handler
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import coil.api.load
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler
import com.dancing_koala.clairdelune.R
import com.dancing_koala.clairdelune.android.hide
import com.dancing_koala.clairdelune.android.show
import com.dancing_koala.clairdelune.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_home.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class HomeActivity : AppCompatActivity() {
    companion object {
        private const val UI_ANIMATION_DELAY = 300L
    }

    private val hideHandler = Handler()

    private val hideTopPartRunnable = Runnable {
        homeMainImageView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val showTopPart = Runnable {
        homeToolbar.visibility = View.VISIBLE
        homeBottomToolbar.visibility = View.VISIBLE
    }

    private var isFullScreen: Boolean = false
    private val hideRunnable = Runnable { hide() }

    private val viewModel: HomeViewModel by viewModels()

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
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        isFullScreen = true
        delayedHide(100L)

        viewModel.viewStateLiveData.observe(this, Observer {
            when (it) {
                HomeViewModel.ViewState.Loading      -> showLoading()
                is HomeViewModel.ViewState.ShowImage -> showImage(it.imageUrl)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        viewModel.start()
    }

    private fun showLoading() {
        homeLoadingIndicator.show()
    }

    private fun hideLoading() {
        homeLoadingIndicator.hide()
    }

    private fun showImage(url: String) {
        hideLoading()
        homeMainImageView.load(url)
    }

    private fun toggle() = if (isFullScreen) hide() else show()

    private fun hide() {
        // Hide UI first
        homeToolbar.visibility = View.GONE
        homeBottomToolbar.visibility = View.GONE
        isFullScreen = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showTopPart)
        hideHandler.postDelayed(hideTopPartRunnable, UI_ANIMATION_DELAY)
    }

    private fun show() {
        // Show the system bar
        homeMainImageView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        isFullScreen = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hideTopPartRunnable)
        hideHandler.postDelayed(showTopPart, UI_ANIMATION_DELAY)
    }

    private fun delayedHide(delayMillis: Long) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis)
    }
}
