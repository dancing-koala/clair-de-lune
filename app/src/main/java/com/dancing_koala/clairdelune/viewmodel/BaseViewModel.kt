package com.dancing_koala.clairdelune.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dancing_koala.clairdelune.android.App

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    protected open val kodein = (application as App).kodein
}