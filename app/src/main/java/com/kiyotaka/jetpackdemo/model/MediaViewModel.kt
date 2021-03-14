package com.kiyotaka.jetpackdemo.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.kiyotaka.jetpackdemo.http.PostMedia
import com.kiyotaka.jetpackdemo.http.ResultException
import com.kiyotaka.jetpackdemo.http.RetrofitClient
import com.kiyotaka.jetpackdemo.ui.activity.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class MediaViewModel : ViewModel() {

    val isShow = MutableLiveData(false)
    val musicPath = MutableLiveData("")
    val MP3Name = MutableLiveData("")

    fun queryMP3() {
        val postMedia = PostMedia(MainActivity.phone.toLong())
        val gson = Gson()
        val json = gson.toJson(postMedia)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    RetrofitClient.reqApi.postMedia(body)
                }
                if(data.code == 200 || data.code == 201){
                    if(!data.data[0].status){
                        ToastUtils.showShort("您还未设置铃声！")
                    }else {
                        MP3Name.value = data.data[0].mp3.substringAfterLast("/")
                    }
                }
            } catch (e: Throwable) {
                Log.e("music",e.message)
                e.printStackTrace()
                if (e is ResultException) {
                    ToastUtils.showShort(e.msg)
                } else {
                    ToastUtils.showShort("服务器异常，请稍后再试")
                }
            }
        }
    }


    fun uploadMP3() {
        if (musicPath.value == null || musicPath.value == "") {
            ToastUtils.showShort("请选择音频文件提交")
            return
        } else {
            val requestBodyBuilder = MultipartBody.Builder()
            try {
                val musicFile = File(musicPath.value!!)
                val musicBody: RequestBody =
                    RequestBody.create("audio/mp3".toMediaTypeOrNull(), musicFile)
                requestBodyBuilder.setType(MultipartBody.FORM)
                requestBodyBuilder.addFormDataPart("mp3", musicFile.name, musicBody)

                requestBodyBuilder.addFormDataPart("phone", MainActivity.phone)

                val requestBody: RequestBody = requestBodyBuilder.build()
                viewModelScope.launch {
                    try {
                        val data = withContext(Dispatchers.IO) {
                            RetrofitClient.reqApi.postMusic(requestBody)
                        }
                        if(data.code == 200 || data.code == 201){
                            ToastUtils.showShort("上传成功")
                        }
                    } catch (e: Throwable) {
                        Log.e("http", e.message)
                        e.printStackTrace()
                        if (e is ResultException) {
                            ToastUtils.showShort(e.msg)
                        } else {
                            ToastUtils.showShort("好像出了些问题，请等等再试试吧")
                        }
                    }
                }
            } catch (e: Throwable) {
                ToastUtils.showShort("获取文件失败！")
            }
        }
    }
}