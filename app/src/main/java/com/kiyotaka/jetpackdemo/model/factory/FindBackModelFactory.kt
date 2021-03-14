package com.kiyotaka.jetpackdemo.model.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kiyotaka.jetpackdemo.model.FindBackViewModel

class FindBackModelFactory():
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FindBackViewModel() as T
    }
}