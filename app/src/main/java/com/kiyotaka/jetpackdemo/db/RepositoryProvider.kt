package com.kiyotaka.jetpackdemo.db

import android.content.Context
import com.kiyotaka.jetpackdemo.db.repository.RecordRepository
import com.kiyotaka.jetpackdemo.db.repository.UserRepository

object RepositoryProvider {

    /**
     * 得到用户仓库
     */
    fun providerUserRepository(context: Context): UserRepository {
        return UserRepository.getInstance(AppDataBase.getInstance(context).userDao())
    }

    /**
     * 记录仓库
     */
    fun providerRecordRepository(context: Context):RecordRepository{
        return RecordRepository.getInstance(AppDataBase.getInstance(context).recordDao())
    }
}