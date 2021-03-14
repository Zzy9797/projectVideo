package com.kiyotaka.jetpackdemo.http

import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface APIs {

    @POST("api/v1/user/sms")  //验证码
    suspend fun getSmsCode(@Body requestBody: RequestBody): BaseBean<String>

    @POST("api/v1/app/user/create")  //注册
    suspend fun postUser(@Body requestBody: RequestBody): BaseBean<String>

    @POST("api/v1/app/user/login")   //登录
    suspend fun login(@Body requestBody: RequestBody): BaseBean<List<LoginResponse>>

    @POST("api/v1/app/user/findpwd")  //找回密码
    suspend fun findBack(@Body requestBody: RequestBody): BaseBean<String>

    @POST("api/v1/app/user/online")  //在线状态
    suspend fun postOnline(@Body requestBody: RequestBody): BaseBean<String>

    @POST("api/v1/app/user/setbash") //上传资料
    suspend fun postBash(@Body requestBody: RequestBody): BaseBean<List<BashResponse>>

    @POST("api/v1/app/user/bash") //查询资料
    suspend fun queryBash(@Body requestBody: RequestBody): BaseBean<List<BashResponse>>

    @POST("api/v1/app/user/setpwd")  //修改密码
    suspend fun changeKey(@Body requestBody: RequestBody): BaseBean<String>

    @POST("api/v1/app/user/check")  //查询审核结果（不带原因）
    suspend fun postCheck(@Body requestBody: RequestBody): BaseBean<String>

    @POST("api/v1/app/user/check2") //查询审核结果（带原因)
    suspend fun postCheck2(@Body requestBody: RequestBody): BaseBean<List<VerifyResponse2>>

    @POST("api/v1/consult/query") //查询记录
    suspend fun postConsult(@Body requestBody: RequestBody): BaseBean<List<ConsultResponse>>

    @POST("api/v1/consult/query/one") //查询详细记录
    suspend fun postDetail(@Body requestBody: RequestBody): BaseBean<List<DetailResponse>>

    @POST("api/v1/consult/statistics/all") //全部统计
    suspend fun postStatisticsAll(@Body requestBody: RequestBody): BaseBean<List<StatisticsAllResponse>>

    @POST("api/v1/consult/statistics") //月份统计
    suspend fun postStatistics(@Body requestBody: RequestBody): BaseBean<List<StatisticsMonthResponse>>

    @POST("api/v1/consult/statistics") //月份统计
    fun postStatisticsmain(@Body requestBody: RequestBody): BaseBean<List<StatisticsMonthResponse>>

    @GET("api/v1/app/area/all") //获取擅长领域
    suspend fun getAreaAll(): BaseBean<ArrayList<Area>>

    @POST("api/v1/app/user/checkup") //是否是第一次提交
    suspend fun postCheckUp(@Body requestBody: RequestBody): BaseBean<Boolean>

    @POST("api/v1/app/user/downmp3")//查询铃声
    suspend fun postMedia(@Body requestBody: RequestBody):BaseBean<List<MediaResponse>>

    @POST("api/v1/app/user/upmp3")//上传铃声
    suspend fun postMusic(@Body requestBody: RequestBody):BaseBean<Boolean>

    @GET("/api/v1/app/city/all")//获取城市信息
    suspend fun getCity():BaseBean<ArrayList<CityData>>

    @POST("/api/v1/app/user/checkrecord") // 审核记录
    suspend fun getCheckRecord(@Body requestBody: RequestBody):BaseBean<List<CheckRecordResponse>>

    @POST("/api/v1/app/user/checkrecord/one") //审核记录id
    suspend fun getCheckRecordById(@Body requestBody: RequestBody):BaseBean<List<CheckRecordDetail>>

    @POST("api/v1/app/user/checkonline") //网络轮询 直接用rxjava，不要用自定义的解析
    fun postCheckOnline(@Body requestBody: RequestBody): Observable<Any>

    @GET("api/v1/app/function") //app介绍
    fun postFunctionApp() : Observable<BaseBean<ArrayList<AppContent>>>

    @POST("api/v1/app/procedure") //版本更新
    fun postVersionCheck(@Body requestBody: RequestBody): Observable<BaseBean<ArrayList<VersionResponse>>>

    @POST("api/v1/app/feedback") //意见反馈
    fun postSuggestion(@Body requestBody: RequestBody) : Observable<BaseBean<String>>
}