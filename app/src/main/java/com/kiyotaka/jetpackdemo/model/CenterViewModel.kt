package com.kiyotaka.jetpackdemo.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.kiyotaka.jetpackdemo.db.repository.UserRepository
import com.kiyotaka.jetpackdemo.http.PostBash
import com.kiyotaka.jetpackdemo.http.ResultException
import com.kiyotaka.jetpackdemo.http.RetrofitClient
import com.kiyotaka.jetpackdemo.ui.activity.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class CenterViewModel constructor(
    repository: UserRepository
) : ViewModel() {

    val isShow = MutableLiveData(true)
    val welcome = MutableLiveData("欢迎您！")
    val imageUrl = MutableLiveData("")
    val isQuery = MutableLiveData(false)

    fun queryBash(){
        val postBash = PostBash(MainActivity.phone.toLong())
        val gson = Gson()
        val json = gson.toJson(postBash)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        viewModelScope.launch {
            try{
                val data = withContext(Dispatchers.IO) {
                    RetrofitClient.reqApi.queryBash(body)
                }
                if(data.code == 200){
                    welcome.value = "欢迎您！${data.data[0].name}"
                    imageUrl.value = "http://${data.data[0].head_portrait}"
                    isQuery.value = true
                }
            }catch (e:Throwable){
                isQuery.value = false
                e.printStackTrace()
                Log.e("center",e.message)
                if (e is ResultException) {
                    ToastUtils.showShort(e.msg)
                } else {
                    ToastUtils.showShort("服务器异常，请稍后再试")
                }
            }
        }
    }

}