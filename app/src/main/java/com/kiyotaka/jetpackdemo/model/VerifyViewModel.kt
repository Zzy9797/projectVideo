package com.kiyotaka.jetpackdemo.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.kiyotaka.jetpackdemo.http.CheckRecordID
import com.kiyotaka.jetpackdemo.http.PostVerify
import com.kiyotaka.jetpackdemo.http.ResultException
import com.kiyotaka.jetpackdemo.http.RetrofitClient
import com.kiyotaka.jetpackdemo.ui.activity.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class VerifyViewModel : ViewModel() {

    val isShow = MutableLiveData(false)
    val status = MutableLiveData("加载中")
    val reason = MutableLiveData("加载中")
    val create = MutableLiveData("加载中")


    fun queryStatus() {
        val postVerify2 = PostVerify(
            MainActivity.phone.toLong()
        )
        val gson = Gson()
        val json = gson.toJson(postVerify2)
        val body: RequestBody =
            json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    RetrofitClient.reqApi.postCheck2(body)
                }
                if (data.code == 200 || data.code == 201) {
                    status.value = data.data[0].status
                    reason.value = data.data[0].reason
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                Log.e("verify", e.message)
                if (e is ResultException) {
                    ToastUtils.showShort(e.msg)
                } else {
                    ToastUtils.showShort("服务器异常，请稍后再试")
                }
            }
        }
    }

    fun queryRecord(id: Int) {
        val postVerify2 = CheckRecordID(id)
        val gson = Gson()
        val json = gson.toJson(postVerify2)
        val body: RequestBody =
            json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    RetrofitClient.reqApi.getCheckRecordById(body)
                }
                if (data.code == 200 || data.code == 201) {
                    status.value = when (data.data[0].check_status) {
                        -1 -> "不通过"
                        0 -> "审核中"
                        1 -> "审核通过"
                        else -> "加载中"
                    }
                    reason.value = data.data[0].check_result
                    create.value = data.data[0].create_time
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                Log.e("verify", e.message)
                if (e is ResultException) {
                    ToastUtils.showShort(e.msg)
                } else {
                    ToastUtils.showShort("服务器异常，请稍后再试")
                }
            }
        }
    }
}
