package com.kiyotaka.jetpackdemo.model.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kiyotaka.jetpackdemo.db.repository.UserRepository
import com.kiyotaka.jetpackdemo.model.DataViewModel

class DataModelFactory(
    private var repository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DataViewModel(repository) as T
    }
}