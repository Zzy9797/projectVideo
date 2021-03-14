package com.kiyotaka.jetpackdemo.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.kiyotaka.jetpackdemo.db.repository.RecordRepository
import com.kiyotaka.jetpackdemo.http.*
import com.kiyotaka.jetpackdemo.ui.activity.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel constructor(
    repository: RecordRepository
) : ViewModel() {

    val isShow = MutableLiveData(true)
    val dataList = MutableLiveData<List<ConsultResponse>>()
    val isQuery = MutableLiveData(false)
    val isCheck = MutableLiveData(false)
    val checkResultCode = MutableLiveData(-1)
    val checkResult = MutableLiveData("")
    val isOnline  = MutableLiveData(true)

    val day = MutableLiveData("")
    val week = MutableLiveData("")

    fun queryConsults() {
        val postConsult = PostConsult(MainActivity.phone.toLong())
        val gson = Gson()
        val json = gson.toJson(postConsult)
        val body: RequestBody =
            json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    RetrofitClient.reqApi.postConsult(body)
                }
                if (data.code == 200) {
                    dataList.value = data.data
                    isQuery.value = true
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                if (e is ResultException) {
                    ToastUtils.showShort(e.msg)
                }
            }
        }
    }

    fun queryCheck() {
        val postVerify = PostVerify(
            MainActivity.phone.toLong()
        )
        val gson = Gson()
        val json = gson.toJson(postVerify)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    RetrofitClient.reqApi.postCheck2(body)
                }
                if (data.code == 200) {
                    when (data.data[0].status) {
                        "审核不通过" -> {
                            checkResultCode.value = -1
                        }
                        "审核中" -> {
                            checkResultCode.value = 0
                        }
                        "审核通过" -> {
                            checkResultCode.value = 1
                        }
                    }
                }
            } catch (e: Throwable) {
                checkResultCode.value = 0
                e.printStackTrace()
                if (e is ResultException) {
                    ToastUtils.showShort(e.msg)
                }
            } finally {
                isCheck.value = true
            }
        }
    }

    fun isOnline(int: Int){
        val onLine = OnLine(
            MainActivity.phone.toLong(),
            int
        )
        val gson = Gson()
        val json = gson.toJson(onLine)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    RetrofitClient.reqApi.postOnline(body)
                }
                if (data.code == 200) {
                    ToastUtils.showShort("切换成功")
                }
            } catch (e: Throwable) {
                checkResultCode.value = 0
                e.printStackTrace()
                if (e is ResultException) {
                    ToastUtils.showShort(e.msg)
                }
            }
        }
    }

    fun queryStaticday(){
        val date = Date()
        val dateFormat= SimpleDateFormat("YYYY-MM-dd")
        val sstring = "${dateFormat.format(date)} 00:00:00"
        val estring = "${dateFormat.format(date)} 23:59:59"
        val postStatisticsMonth = PostStatistics(
            MainActivity.phone.toLong(),
            sstring,
            estring
        )

        val gson = Gson()
        val json = gson.toJson(postStatisticsMonth)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO){
                    RetrofitClient.reqApi.postStatistics(body)
                }
                if(data.code == 200){
                    day.value = "本日咨询人数：${data.data[0].number}"
                }
            } catch (e: Throwable) {
                day.value = "本日咨询人数：0人"
                e.printStackTrace()
                if (e is ResultException) {

                    ToastUtils.showShort(e.msg)
                }
            }
        }
    }

    fun queryStaticweek(){
        val date = Date()
        val dateFormat= SimpleDateFormat("YYYY-MM-dd")

        val ca = Calendar.getInstance()
        ca.set(Calendar.DAY_OF_YEAR, ca.get(Calendar.DAY_OF_YEAR) - 7)

        val sstring = "${dateFormat.format(ca.time)} 00:00:00"
        val estring = "${dateFormat.format(date)} 23:59:59"
        val postStatisticsMonth = PostStatistics(
            MainActivity.phone.toLong(),
            sstring,
            estring
        )
        Log.e("d",postStatisticsMonth.toString())
        val gson = Gson()
        val json = gson.toJson(postStatisticsMonth)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO){
                    RetrofitClient.reqApi.postStatistics(body)
                }
                if(data.code == 200){
                    week.value = "近7天咨询人数：${data.data[0].number.toString()}"
                }
            } catch (e: Throwable) {
                week.value = "近7天咨询人数：0人"
                e.printStackTrace()
                if (e is ResultException) {

                    ToastUtils.showShort(e.msg)
                }
            }
        }
    }
}