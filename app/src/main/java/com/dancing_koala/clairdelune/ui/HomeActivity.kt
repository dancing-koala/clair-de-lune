package com.dancing_koala.clairdelune.ui

import android.content.Intent
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import coil.api.load
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler
import com.dancing_koala.clairdelune.R
import com.dancing_koala.clairdelune.android.hide
import com.dancing_koala.clairdelune.android.show
import com.dancing_koala.clairdelune.viewmodel.HomeViewModel
import com.google.android.material.snackbar.Snackbar
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
    private var isFullScreen: Boolean = false
    private val hideRunnable = Runnable { hideUI() }

    private val viewModel: HomeViewModel by viewModels()

    private val imageMatrixTouchHandler by lazy { ImageMatrixTouchHandler(this) }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        homePictureTitle.setOnClickListener { viewModel.onUserNameClick() }
        homeDownloadButton.setOnClickListener { viewModel.onDownloadButtonClick() }

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
//        delayedHide(100L)

        viewModel.viewStateLiveData.observe(this, Observer {
            when (it) {
                HomeViewModel.ViewState.Loading                  -> showLoading()
                is HomeViewModel.ViewState.ShowImage             -> showImage(it.imageUrl)
                is HomeViewModel.ViewState.ShowUserName          -> showUserName(it.userName)
                is HomeViewModel.ViewState.ShowErrorMessage      -> showError(it.message)
                is HomeViewModel.ViewState.ShowUserProfileScreen -> showUserProfile(it.userProfileUrl)
                is HomeViewModel.ViewState.ShowMessage           -> showMessage(it.message)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        viewModel.start()

        homeMainImageView.imageMatrix = Matrix()
        homeMainImageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
    }

    private fun showUserProfile(userProfileUrl: String) {
        startActivity(
            Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(userProfileUrl) }
        )
    }

    private fun showUserName(userName: String) {
        val underlineSpan = UnderlineSpan()
        val spannable = SpannableString(getString(R.string.home_bottom_title_template, userName))
        spannable.setSpan(underlineSpan, spannable.length - userName.length - 1, spannable.length, 0)
        homePictureTitle.text = spannable
    }

    private fun showError(message: String) =
        Snackbar.make(homeRootContainer, message, Snackbar.LENGTH_LONG).show()

    private fun showMessage(message: String) =
        Snackbar.make(homeRootContainer, message, Snackbar.LENGTH_LONG).show()

    private fun showLoading() = homeLoadingIndicator.show()

    private fun hideLoading() = homeLoadingIndicator.hide()

    private fun showImage(url: String) {
        hideLoading()
        homeMainImageView.load(url)
    }

    private fun toggle() = if (isFullScreen) hideUI() else showUI()

    private fun hideUI() {
        homeToolbar.visibility = View.GONE
        homeBottomToolbar.visibility = View.GONE
        isFullScreen = false

        hideHandler.removeCallbacks(showTopPart)
        hideHandler.postDelayed(hideTopPartRunnable, UI_ANIMATION_DELAY)
    }

    private fun showUI() {
        homeMainImageView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        isFullScreen = true

        hideHandler.removeCallbacks(hideTopPartRunnable)
        hideHandler.postDelayed(showTopPart, UI_ANIMATION_DELAY)
    }

    private fun delayedHide(delayMillis: Long) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis)
    }
}
