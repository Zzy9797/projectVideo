package com.kiyotaka.jetpackdemo.model.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kiyotaka.jetpackdemo.db.repository.UserRepository
import com.kiyotaka.jetpackdemo.model.LoginViewModel


class LoginModelFactory(private var repository: UserRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(repository) as T
    }
}