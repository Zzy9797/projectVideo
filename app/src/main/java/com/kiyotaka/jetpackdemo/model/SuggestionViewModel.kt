package com.kiyotaka.jetpackdemo.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SuggestionViewModel : ViewModel() {

    val isShow = MutableLiveData(false)
}