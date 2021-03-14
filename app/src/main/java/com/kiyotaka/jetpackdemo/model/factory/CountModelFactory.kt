package com.kiyotaka.jetpackdemo.model.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kiyotaka.jetpackdemo.model.ChangeKeyViewModel
import com.kiyotaka.jetpackdemo.model.CountViewModel

class CountModelFactory: ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CountViewModel() as T
    }
}