package com.kiyotaka.jetpackdemo.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.kiyotaka.jetpackdemo.db.repository.RecordRepository
import com.kiyotaka.jetpackdemo.http.ConsultResponse
import com.kiyotaka.jetpackdemo.http.PostConsult
import com.kiyotaka.jetpackdemo.http.ResultException
import com.kiyotaka.jetpackdemo.http.RetrofitClient
import com.kiyotaka.jetpackdemo.ui.activity.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class RecordViewModel constructor(
    private val repository: RecordRepository
) : ViewModel() {

    val isShow = MutableLiveData(false)
    val dataList = MutableLiveData<List<ConsultResponse>>()
    val isQuery = MutableLiveData(false)

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
                } else {
                    ToastUtils.showShort("服务器异常，请稍后再试")
                }
            }
        }
    }
}
