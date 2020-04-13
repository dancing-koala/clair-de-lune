package com.dancing_koala.clairdelune.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dancing_koala.clairdelune.android.AppDownloadManager
import com.dancing_koala.clairdelune.android.BaseViewModel
import com.dancing_koala.clairdelune.core.LockWithPicture
import com.dancing_koala.clairdelune.core.Result
import com.dancing_koala.clairdelune.data.LockWithPictureDataSource
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance

class HomeViewModel(application: Application) : BaseViewModel(application) {

    val viewStateLiveData: LiveData<ViewState>
        get() = _viewStateLiveData

    val lockWithPictureListLiveData: LiveData<List<LockWithPicture>>
        get() = _lockWithPictureListLiveData

    val selectedLockWithPictureLiveData: LiveData<LockWithPicture>
        get() = _lockWithPictureLiveData

    private val _viewStateLiveData = MutableLiveData<ViewState>()
    private val _lockWithPictureLiveData = MutableLiveData<LockWithPicture>()
    private val _lockWithPictureListLiveData = MutableLiveData<List<LockWithPicture>>()

    private val lockWithPictureDataSource: LockWithPictureDataSource by kodein.instance()

    private val appDownloadManager by lazy {
        AppDownloadManager(application)
    }

    fun start() {
        _viewStateLiveData.value = ViewState.Loading

        viewModelScope.launch {
            val result = lockWithPictureDataSource.getLockWithPictureListUpToToday()

            if (result is Result.Success<List<LockWithPicture>>) {
                result.data.let {
                    _lockWithPictureListLiveData.value = it
                    _lockWithPictureLiveData.value = it.first()
                }
            } else {
                _viewStateLiveData.value = ViewState.ShowErrorMessage("Une erreur est survenue")
            }
        }
    }

    fun onUserNameClick() {
        _lockWithPictureLiveData.value?.let { lockWithPicture ->
            _viewStateLiveData.value = ViewState.ShowUserProfileScreen(
                lockWithPicture.picture.user.links.html
            )
        }
    }

    fun onDownloadButtonClick() {
        _lockWithPictureLiveData.value?.let { lockWithPicture ->
            val fileName = "ClaireDeLune-${lockWithPicture.picture.id}"
            appDownloadManager.downloadFile(lockWithPicture.picture.links.download, fileName)
            _viewStateLiveData.value = ViewState.ShowMessage("Téléchargement de l'image lancé  ❤️")
        }
    }

    fun onInfoButtonClick() {
        _viewStateLiveData.value = ViewState.ShowInfoScreen
    }

    fun onPictureListButtonClick() {
        _viewStateLiveData.value = ViewState.ShowPictureListScreen
    }

    fun onPictureWebsiteLinkClick() {
        _lockWithPictureLiveData.value?.picture?.links?.html?.let {
            _viewStateLiveData.value = ViewState.ShowUserPictureWebScreen(it)
        }
    }

    fun onUserPortfolioLinkClick() {
        _lockWithPictureLiveData.value?.picture?.user?.portfolioUrl?.let {
            _viewStateLiveData.value = ViewState.ShowUserPortfolioScreen(it)
        }
    }

    fun onNewPictureSelected(newPosition: Int) {
        _lockWithPictureListLiveData.value?.let { lockWithPictureList ->
            val item = lockWithPictureList[newPosition]
            _lockWithPictureLiveData.value = item
        }
    }

    sealed class ViewState {
        object Loading : ViewState()
        class ShowErrorMessage(val message: String) : ViewState()
        class ShowMessage(val message: String) : ViewState()
        class ShowUserProfileScreen(val userProfileUrl: String) : ViewState()
        class ShowUserPictureWebScreen(val pictureUrl: String) : ViewState()
        class ShowUserPortfolioScreen(val portfolioUrl: String) : ViewState()
        object ShowInfoScreen : ViewState()
        object ShowPictureListScreen : ViewState()
    }
}