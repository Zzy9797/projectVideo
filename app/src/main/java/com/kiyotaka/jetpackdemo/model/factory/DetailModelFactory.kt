package com.kiyotaka.jetpackdemo.model.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kiyotaka.jetpackdemo.db.repository.RecordRepository
import com.kiyotaka.jetpackdemo.model.DetailViewModel

class DetailModelFactory constructor(private val repository: RecordRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DetailViewModel(repository) as T
    }
}