package com.kiyotaka.jetpackdemo.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.kiyotaka.jetpackdemo.http.PostStatistics
import com.kiyotaka.jetpackdemo.http.PostStatisticsAll
import com.kiyotaka.jetpackdemo.http.ResultException
import com.kiyotaka.jetpackdemo.http.RetrofitClient
import com.kiyotaka.jetpackdemo.ui.activity.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.*

class CountViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    val isShow = MutableLiveData(false)
    val numberAll = MutableLiveData("")
    val timeAll = MutableLiveData("")
    val evaluateAll = MutableLiveData("")
    val numberMonth = MutableLiveData("")
    val timeMonth = MutableLiveData("")
    val evaluateMonth = MutableLiveData("")
    val peopleMonth = MutableLiveData("")

    val startDate = MutableLiveData("")
    val showStartDate = MutableLiveData("")
    val endDate = MutableLiveData("")
    val showEndDate = MutableLiveData("")

    val pickerYear = MutableLiveData(0)
    val pickerMonth = MutableLiveData(0)
    val pickerDay = MutableLiveData(0)

    /**
     * initData
     */
    fun initData() {
        val cd = Calendar.getInstance()
        pickerYear.value = cd.get(Calendar.YEAR)
        pickerMonth.value = cd.get(Calendar.MONTH)
        pickerDay.value = cd.get(Calendar.DATE)
        showStartDate.value = "${pickerYear.value}-${pickerMonth.value!! + 1}-${pickerDay.value}"
        showEndDate.value = "${pickerYear.value}-${pickerMonth.value!! + 1}-${pickerDay.value}"


        val sdate = "${showStartDate.value} 00:00:00"
        val edate = "${showEndDate.value} 23:59:59"

        Log.e("date", "$sdate  $edate")
        queryMonth(sdate, edate)
    }

    /**
     * 查询所有
     */
    fun queryAll() {
        val postStatisticsAll = PostStatisticsAll(
            MainActivity.phone.toLong()
        )
        val gson = Gson()
        val json = gson.toJson(postStatisticsAll)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    RetrofitClient.reqApi.postStatisticsAll(body)
                }
                if (data.code == 200) {
                    numberAll.value = data.data[0].number.toString() + " 次"
                    timeAll.value = (data.data[0].time / 60).toString() + " 分钟"
                    evaluateAll.value = data.data[0].evaluate.toString() + " / 10.0"
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                numberAll.value = "0 次"
                timeAll.value = "0 分钟"
                evaluateAll.value = "0.0 / 10.0"
                if (e is ResultException) {
                    ToastUtils.showShort(e.msg)
                }
            }
        }
    }


    /**
     * 查询月份
     */
    fun queryMonth(sdate: String, edate: String) {
        val postStatisticsMonth = PostStatistics(
            MainActivity.phone.toLong(),
            sdate,
            edate
        )
        val gson = Gson()
        val json = gson.toJson(postStatisticsMonth)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    RetrofitClient.reqApi.postStatistics(body)
                }
                if (data.code == 200) {
                    if(data.data.isNotEmpty()) {
                        numberMonth.value = data.data[0].number.toString() + " 次"
                        timeMonth.value = (data.data[0].time / 60).toString() + " 分钟"
                        evaluateMonth.value = data.data[0].evaluate.toString() + " / 10.0"
                        peopleMonth.value = data.data[0].people_number.toString() + " 人"
                    }else{
                        ToastUtils.showShort("查无记录")
                        numberMonth.value = "0 次"
                        timeMonth.value = "0 分钟"
                        evaluateMonth.value = "0.0 / 10.0"
                        peopleMonth.value =  "0 人"
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                numberMonth.value = "0 次"
                timeMonth.value = "0 分钟"
                evaluateMonth.value = "0.0 / 10.0"
                peopleMonth.value =  "0 人"
                if (e is ResultException) {
                    ToastUtils.showShort(e.msg)
                }
            }
        }
    }
}
