package com.kiyotaka.jetpackdemo.model.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kiyotaka.jetpackdemo.model.SettingViewModel

class SettingModelFactory : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingViewModel() as T
    }
}