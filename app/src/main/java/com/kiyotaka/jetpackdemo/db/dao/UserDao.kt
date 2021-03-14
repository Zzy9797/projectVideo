package com.kiyotaka.jetpackdemo.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kiyotaka.jetpackdemo.db.data.User

/**
 * 用户Dao
 */
@Dao
interface UserDao {
    @Insert
    fun insertUser(user: User): Long

    @Delete
    fun deleteUser(user: User)

    @Update
    fun updateUser(user: User)

    @Query("SELECT * FROM user WHERE id = :id")
    fun findUserById(id: Int): LiveData<User?>

    @Query("SELECT * FROM user WHERE user_phone=:phone")
    fun findUserByPhone(phone: String): LiveData<User?>

}