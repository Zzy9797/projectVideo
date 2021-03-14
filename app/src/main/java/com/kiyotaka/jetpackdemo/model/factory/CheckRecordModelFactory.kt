package com.kiyotaka.jetpackdemo.model.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kiyotaka.jetpackdemo.model.CheckViewModel

class CheckRecordModelFactory : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CheckViewModel() as T
    }
}