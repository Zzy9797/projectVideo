package com.kiyotaka.jetpackdemo.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.kiyotaka.jetpackdemo.db.repository.RecordRepository
import com.kiyotaka.jetpackdemo.http.PostDetail
import com.kiyotaka.jetpackdemo.http.ResultException
import com.kiyotaka.jetpackdemo.http.RetrofitClient
import com.kiyotaka.jetpackdemo.ui.activity.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class DetailViewModel constructor(
    repository: RecordRepository
) : ViewModel() {
    // TODO: Implement the ViewModel

    val isShow = MutableLiveData(false)
    val id = MutableLiveData("")
    val createTime = MutableLiveData("")
    val startTime = MutableLiveData("")
    val endTime = MutableLiveData("")
    val duration = MutableLiveData("")
    val evaluate = MutableLiveData("")

    fun queryDetail(id: Int) {
        val postDetail = PostDetail(
            id
        )
        val gson = Gson()
        val json = gson.toJson(postDetail)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    RetrofitClient.reqApi.postDetail(body)
                }
                if (data.code == 200 || data.code == 201) {
                    createTime.value = data.data[0].create_time
                    startTime.value = translate(data.data[0].start_time)
                    endTime.value = translate(data.data[0].end_time)
                    duration.value = data.data[0].duration.toString()
                    evaluate.value = data.data[0].evaluate.toString()
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

    private fun translate(str: String): String {
        val data = str.split(" ")
        return if(data.size == 2) {
            data[0] + "\n" + data[1]
        }else{
            str
        }
    }
}
