package com.kiyotaka.jetpackdemo.db.repository

import com.kiyotaka.jetpackdemo.db.dao.RecordDao
import com.kiyotaka.jetpackdemo.db.data.Record
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class RecordRepository private constructor(private val recordDao: RecordDao) {

    //获取全部记录
    fun getAllRecord() = recordDao.queryAllRecord()

    //新建记录
    suspend fun insertUser(record: Record): Long {
        return withContext(IO) {
            recordDao.insertUser(record)
        }
    }

    //更新记录
    suspend fun updateUser(record: Record) {
        withContext(IO) {
            recordDao.insertUser(record)
        }
    }

    companion object {
        @Volatile
        private var instance: RecordRepository? = null

        fun getInstance(recordDao: RecordDao): RecordRepository =
            instance ?: synchronized(this) {
                instance
                    ?: RecordRepository(recordDao).also {
                        instance = it
                    }
            }
    }
}