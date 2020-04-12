package com.dancing_koala.clairdelune.android

import android.app.Application
import androidx.lifecycle.AndroidViewModel

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    protected open val kodein = (application as App).kodein
}