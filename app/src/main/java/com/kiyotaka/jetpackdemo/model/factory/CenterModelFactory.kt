package com.kiyotaka.jetpackdemo.model.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kiyotaka.jetpackdemo.db.repository.UserRepository
import com.kiyotaka.jetpackdemo.model.CenterViewModel

class CenterModelFactory (private var repository: UserRepository):
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CenterViewModel(repository) as T
    }
}