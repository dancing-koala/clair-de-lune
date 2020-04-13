package com.dancing_koala.clairdelune.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dancing_koala.clairdelune.android.AppDownloadManager
import com.dancing_koala.clairdelune.android.BaseViewModel
import com.dancing_koala.clairdelune.core.LockWithPicture
import com.dancing_koala.clairdelune.core.Result
import com.dancing_koala.clairdelune.core.Urls
import com.dancing_koala.clairdelune.data.LockWithPictureDataSource
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance

class HomeViewModel(application: Application) : BaseViewModel(application) {

    val viewStateLiveData: LiveData<ViewState>
        get() = _viewStateLiveData

    private val _viewStateLiveData = MutableLiveData<ViewState>()

    private val lockWithPictureDataSource: LockWithPictureDataSource by kodein.instance()

    private lateinit var lockWithPicture: LockWithPicture

    private val appDownloadManager by lazy {
        AppDownloadManager(application)
    }

    fun start() {
        _viewStateLiveData.value = ViewState.Loading

        viewModelScope.launch {
            val result = lockWithPictureDataSource.getLockWithPictureForToday()

            if (result is Result.Success<LockWithPicture>) {
                lockWithPicture = result.data
                _viewStateLiveData.value = ViewState.ShowImage(
                    lockWithPicture.picture.urls.getBestNonNullUrl()
                )
                _viewStateLiveData.value = ViewState.ShowUserName(
                    lockWithPicture.picture.user.username
                )
            } else {
                _viewStateLiveData.value = ViewState.ShowErrorMessage("Une erreur est survenue")
            }
        }
    }

    private fun Urls.getBestNonNullUrl(): String = when {
        raw != null     -> raw
        full != null    -> full
        regular != null -> regular
        small != null   -> small
        thumb != null   -> thumb
        else            -> ""
    }

    fun onUserNameClick() {
        if (::lockWithPicture.isInitialized) {
            _viewStateLiveData.value = ViewState.ShowUserProfileScreen(
                lockWithPicture.picture.user.links.html
            )
        }
    }

    fun onDownloadButtonClick() {
        if (::lockWithPicture.isInitialized) {
            val fileName = "ClaireDeLune-${lockWithPicture.picture.id}"
            appDownloadManager.downloadFile(
                lockWithPicture.picture.links.download,
                fileName
            )
        }
    }

    sealed class ViewState {
        object Loading : ViewState()
        class ShowImage(val imageUrl: String) : ViewState()
        class ShowUserName(val userName: String) : ViewState()
        class ShowErrorMessage(val message: String) : ViewState()
        class ShowUserProfileScreen(val userProfileUrl: String) : ViewState()
    }
}