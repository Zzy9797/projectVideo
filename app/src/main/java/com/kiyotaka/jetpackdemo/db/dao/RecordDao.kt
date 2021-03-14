package com.kiyotaka.jetpackdemo.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kiyotaka.jetpackdemo.db.data.Record
import com.kiyotaka.jetpackdemo.db.data.User

/**
 * 记录Dao
 */
@Dao
interface RecordDao {
    @Insert
    fun insertUser(record: Record): Long

    @Delete
    fun deleteUser(record: Record)

    @Update
    fun updateUser(record: Record)

    @Query("SELECT * FROM record")
    fun queryAllRecord(): List<Record>
}