package com.kiyotaka.jetpackdemo.db.repository

import androidx.lifecycle.LiveData
import com.kiyotaka.jetpackdemo.db.dao.UserDao
import com.kiyotaka.jetpackdemo.db.data.User
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class UserRepository private constructor(private val userDao: UserDao) {

    //根据phone获取当前用户
    fun findUserByPhone(phone: String): LiveData<User?> = userDao.findUserByPhone("1")

    fun findUserById(id:Int):LiveData<User?> = userDao.findUserById(id)

    //新建用户
    suspend fun insertUser(): Long {
        return withContext(IO) {
            userDao.insertUser(User(null, "1", null, 1, "name", 1, null, null, null, null))
        }
    }

    //更新用户
    suspend fun updateUser(user: User) {
        withContext(IO) {
            userDao.insertUser(user)
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(userDao: UserDao): UserRepository =
            instance ?: synchronized(this) {
                instance
                    ?: UserRepository(userDao).also {
                        instance = it
                    }
            }
    }
}