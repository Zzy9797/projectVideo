package com.kiyotaka.jetpackdemo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kiyotaka.jetpackdemo.db.dao.RecordDao
import com.kiyotaka.jetpackdemo.db.dao.UserDao
import com.kiyotaka.jetpackdemo.db.data.Record
import com.kiyotaka.jetpackdemo.db.data.User

/**
 *数据库文件
 */
@Database(entities = [User::class,Record::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {

    //获取UserDao
    abstract fun userDao(): UserDao

    //获取RecordDao
    abstract fun recordDao():RecordDao

    companion object {
        @Volatile
        private var instance: AppDataBase? = null

        fun getInstance(context: Context):AppDataBase{
            return instance?: synchronized(this){
                instance?:buildDataBase(context).also{
                    instance = it
                }
            }
        }

        private fun buildDataBase(context: Context):AppDataBase{
            return Room
                .databaseBuilder(context,AppDataBase::class.java,"jetPackDemo-database")
                .addCallback(object :RoomDatabase.Callback(){
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                    }
                })
                .build()
        }
    }
}