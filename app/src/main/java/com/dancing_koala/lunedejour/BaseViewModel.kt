package com.dancing_koala.lunedejour

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dancing_koala.App

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    protected open val kodein = (application as App).kodein
}