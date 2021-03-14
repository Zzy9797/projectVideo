package com.kiyotaka.jetpackdemo.db.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Duration


/**
 * 记录表
 */
@Entity(tableName = "record")
data class Record(
    @ColumnInfo(name = "record_user") val user: Int,  //用户
    @ColumnInfo(name = "record_create_time") val create_time: String,  //创建时间
    @ColumnInfo(name = "record_start_time") val start_time: String,  //开始时间
    @ColumnInfo(name = "record_end_time") val end_time: String,  // 结束时间
    @ColumnInfo(name = "record_duration") val duration: String,  //持续时间
    @ColumnInfo(name = "record_evaluate") val evaluate: Int  //好评度
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}