package com.kiyotaka.jetpackdemo.db.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户表
 */
@Entity(tableName = "user")
data class User(
    @ColumnInfo(name = "user_head") val url: String?,//头像地址
    @ColumnInfo(name = "user_phone") val phone: String,  //手机
    @ColumnInfo(name = "user_password") val password: String?,  //密码
    @ColumnInfo(name = "user_identity") val identity: Int, //身份（0用户，1医生，这里都是医生）
    @ColumnInfo(name = "user_name") val name: String?, //姓名
    @ColumnInfo(name = "user_gender") val gender: Int, //性别（0男，1女）
    @ColumnInfo(name = "user_contact") val contact: String?, //联系方式
    @ColumnInfo(name = "user_introduction") val introduction: String?,  //个人介绍
    @ColumnInfo(name = "user_area") val area: String?, //擅长领域
    @ColumnInfo(name = "user_credential") val credential: String?  //专业资质（暂定）
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}