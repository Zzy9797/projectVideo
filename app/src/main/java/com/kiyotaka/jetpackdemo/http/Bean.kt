package com.kiyotaka.jetpackdemo.http


data class BaseBean<T>(var code: Int = 0, var msg: String = "", var data: T)

data class ErrorResponse(val code: Int = 0, val msg: String = "")

data class PostSms(val phone: Long)

data class PostCreate(val phone: Long, val sms_code: Int, val password: String)

data class PostLogin(val phone: Long, val password: String)

data class LoginResponse(val id: Int, val phone: String)

data class PostFindBack(val phone: Long, val new_password: String, val sms_code: Int)

data class PostBash(val phone: Long)

data class BashResponse(
    val phone: Long,
    val head_portrait: String,
    val name: String,
    val gender: Int,
    val agency: String,
    val contact: String,
    val introduction: String,
    val city: String,
    val area_one: String,
    val area_two: String,
    val area_three: String,
    val work_year: Int,
    val credential: String,
    val certificates: String,
    val consult_result: String,
    val education: String,
    val training: String,
    val online: Int
)

data class ChangeKey(val phone: Long, val password: String, val new_password: String)

data class PostVerify(val phone: Long)

data class VerifyResponse2(val status: String, val reason: String)

data class PostConsult(val counselor: Long)

data class ConsultResponse(val id: Int, val user: String, val start_time: String)

data class PostDetail(val id: Int)

data class DetailResponse(
    val user: String,
    val create_time: String,
    val start_time: String,
    val end_time: String,
    val duration: Int,
    val evaluate: Int
)

data class OnLine(val phone: Long, val online: Int)

data class PostStatisticsAll(val counselor: Long)

data class StatisticsAllResponse(val number: Int, val time: Int, val evaluate: Float)

data class PostStatistics(val counselor: Long, val start_time: String, val end_time: String)

data class StatisticsMonthResponse(
    val number: Int,
    val time: Int,
    val evaluate: Float,
    val people_number: Int
)

data class PostYun(val user: String)

data class YunResponse(val code: Int, val msg: String, val data: String)

data class Area(val id: Int, val name: String, val desc: String)

data class CheckUpFirst(val phone: Long)

data class PostMedia(val phone: Long)

data class MediaResponse(val status: Boolean, val mp3: String)

data class ListMedia(val code: Int, val msg: String, val data: List<MediaResponse>)

data class City(val id: Int, val city: String) {
    override fun toString(): String {
        return city
    }
}

data class CityData(val province: String, val city: ArrayList<City>)

data class PostCheckRecord(val phone: Long)

data class CheckRecordResponse(
    val id: Int,
    val phone: Long,
    val check_status: Int,
    val check_result: String,
    val create_time: String
)

data class CheckRecordID(val id: Int)

data class CheckRecordDetail(
    val phone: Long,
    val check_status: Int,
    val check_result: String,
    val create_time: String
)

data class AppContent(
    val content: String
)

data class PostVersion(val version: Int)

data class VersionResponse(val apk_url: String, val version: Int, val desc: String)
