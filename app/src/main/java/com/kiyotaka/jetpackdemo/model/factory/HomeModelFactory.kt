package com.kiyotaka.jetpackdemo.model.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kiyotaka.jetpackdemo.db.repository.RecordRepository
import com.kiyotaka.jetpackdemo.model.HomeViewModel

class HomeModelFactory(private val repository: RecordRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}