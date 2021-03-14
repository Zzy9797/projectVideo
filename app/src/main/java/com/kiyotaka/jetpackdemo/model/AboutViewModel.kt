package com.kiyotaka.jetpackdemo.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AboutViewModel : ViewModel() {

    val isShow = MutableLiveData(false)
}