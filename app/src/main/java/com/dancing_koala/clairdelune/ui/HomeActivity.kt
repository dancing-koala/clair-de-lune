package com.dancing_koala.clairdelune.ui

import android.content.Intent
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler
import com.dancing_koala.clairdelune.R
import com.dancing_koala.clairdelune.android.*
import com.dancing_koala.clairdelune.core.LockWithPicture
import com.dancing_koala.clairdelune.viewmodel.HomeViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.item_picture_archive.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {
    companion object {
        private const val UI_ANIMATION_DELAY = 300L
    }

    private val dateFormatter = SimpleDateFormat("dd/MM/YYYY", Locale.ROOT)
    private val hideHandler = Handler()
    private var isFullScreen: Boolean = false
    private val hideRunnable = Runnable { hideUI() }

    private val viewModel: HomeViewModel by viewModels()

    private val linearSnapHelper = LinearSnapHelper()

    private val imageMatrixTouchHandler by lazy { ImageMatrixTouchHandler(this) }
    private val gestureDetector by lazy {
        GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                toggle()
                return true
            }
        })
    }

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
        homeMainUIGroup.show()
    }

    private val onSnapPositionChangeListener = object : SnapOnScrollListener.OnSnapPositionChangeListener {
        override fun onSnapPositionChange(newPosition: Int) {
            viewModel.onNewPictureSelected(newPosition)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        homePictureTitle.setOnClickListener { viewModel.onUserNameClick() }
        homeDownloadButton.setOnClickListener { viewModel.onDownloadButtonClick() }
        homeInfoButton.setOnClickListener { viewModel.onInfoButtonClick() }

        homePictureListRecyclerView.addItemDecoration(
            SpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.base_margin_xs))
        )
        linearSnapHelper.attachToRecyclerView(homePictureListRecyclerView)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        isFullScreen = true

        viewModel.viewStateLiveData.observe(this, Observer {
            when (it) {
                HomeViewModel.ViewState.Loading                  -> showLoading()
                is HomeViewModel.ViewState.ShowErrorMessage      -> showError(it.message)
                is HomeViewModel.ViewState.ShowUserProfileScreen -> showUserProfile(it.userProfileUrl)
                is HomeViewModel.ViewState.ShowMessage           -> showMessage(it.message)
                HomeViewModel.ViewState.ShowInfoScreen           -> showInfoScreen()
            }
        })

        viewModel.selectedLockWithPictureLiveData.observe(this, Observer {
            showImage(it.picture.urls.getBestNonNullUrl())
            showUserName(it.picture.user.username)
        })

        viewModel.lockWithPictureListLiveData.observe(this, Observer {
            showPictureItems(it.map { item -> item.toPictureItem() })
        })

        delayedHide(250L)

        viewModel.start()
    }

    override fun onStart() {
        super.onStart()
        homeMainImageView.resetMatrix()
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
        showLoading()
        homeMainImageView.resetMatrix()
        homeMainImageView.load(url) {
            listener { _, _ ->
                homeMainImageView.setOnTouchListener { view, event ->
                    gestureDetector.onTouchEvent(event)
                    imageMatrixTouchHandler.onTouch(view, event)
                    true
                }
                hideLoading()
            }
        }
    }

    private fun toggle() = if (isFullScreen) hideUI() else showUI()

    private fun hideUI() {
        homeMainUIGroup.hide()
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

    private fun showInfoScreen() {
        PictureInfoDialogFragment.newInstance().show(supportFragmentManager, "Picture Info")
    }

    private fun showPictureItems(items: List<PictureItem>) {
        if (items.size < 2) {
            homePictureListRecyclerView.layoutManager = null
            homePictureListRecyclerView.adapter = null
            (homePictureListRecyclerView.parent as ViewGroup).apply {
                removeView(homePictureListRecyclerView)
                removeView(homePictureListIndicator)
            }
            homeMainUIGroup.referencedIds = homeMainUIGroup.referencedIds.filter {
                it != R.id.homePictureListRecyclerView
            }.toIntArray()
            return
        }

        homePictureListRecyclerView.apply {
            layoutManager = CenteredLayoutManager(this@HomeActivity, this.width, resources.getDimensionPixelSize(R.dimen.picture_item_height))
            adapter = ItemAdapter(items) { position -> smoothScrollToPosition(position) }
            addOnScrollListener(
                SnapOnScrollListener(
                    linearSnapHelper,
                    SnapOnScrollListener.Behavior.NOTIFY_ON_STATE_IDLE,
                    onSnapPositionChangeListener
                )
            )
            lifecycleScope.launch {
                // Workaround for a quack between CenteredLayoutManager & SnapHelper
                delay(100)
                fling(-minFlingVelocity, 0)
            }
        }
    }

    private fun humanizeDate(date: Date): String =
        dateFormatter.format(date)

    private fun LockWithPicture.toPictureItem(): PictureItem =
        PictureItem(
            itemId = picture.id,
            pictureUrl = picture.urls.small!!,
            pictureDownloadUrl = picture.links.download,
            unlockedDateHumanized = humanizeDate(Date(lock.unlockedAt!!))
        )

    private fun ImageView.resetMatrix() {
        imageMatrix = Matrix()
        scaleType = ImageView.ScaleType.CENTER_INSIDE
    }

    private data class PictureItem(
        val itemId: String,
        val pictureUrl: String,
        val pictureDownloadUrl: String,
        val unlockedDateHumanized: String
    )

    private class ItemAdapter(
        private val data: List<PictureItem>,
        private val onClickBlock: (position: Int) -> Unit
    ) : RecyclerView.Adapter<ItemAdapter.Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_picture_archive, parent, false)
            return Holder(itemView).also { holder ->
                itemView.setOnClickListener {
                    onClickBlock.invoke(holder.adapterPosition)
                }
            }
        }

        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: Holder, position: Int) {
            with(data[position]) {
                holder.image.load(pictureUrl)
                holder.unlockDate.text = unlockedDateHumanized
            }
        }

        class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val image: ImageView = itemView.itemPictureArchiveImage
            val unlockDate: TextView = itemView.itemPictureArchiveUnlockDate
        }

    }
}
