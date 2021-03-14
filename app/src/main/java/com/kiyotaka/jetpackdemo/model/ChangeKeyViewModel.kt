package com.kiyotaka.jetpackdemo.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.kiyotaka.jetpackdemo.http.ChangeKey
import com.kiyotaka.jetpackdemo.http.ResultException
import com.kiyotaka.jetpackdemo.http.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class ChangeKeyViewModel : ViewModel() {

    val isSubmit = MutableLiveData(false)
    val isShow = MutableLiveData(false)
    val phone = MutableLiveData("")
    val password = MutableLiveData("")
    val newPassword = MutableLiveData("")
    val enable = MutableLiveData(false)

    fun onAccountChanged(s: CharSequence) {
        phone.value = s.toString()
        judegEnable()
    }

    fun onPasswordChanged(s: CharSequence) {
        password.value = s.toString()
        judegEnable()
    }

    fun onNewPasswordChanged(s: CharSequence) {
        newPassword.value = s.toString()
        judegEnable()
    }

    fun judegEnable() {
        enable.value = phone.value!!.isNotEmpty()
                && password.value!!.isNotEmpty()
                && newPassword.value!!.isNotEmpty()
    }

    fun onSubmit() {
        if (enable.value!!) {
            val changeKey = ChangeKey(
                phone.value!!.toLong()
                , password.value!!
                , newPassword.value!!
            )
            val gson = Gson()
            val json = gson.toJson(changeKey)
            val body: RequestBody =
                json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            viewModelScope.launch {
                try {
                    val data = withContext(Dispatchers.IO){
                        RetrofitClient.reqApi.changeKey(body)
                    }
                    ToastUtils.showShort(data.msg)
                    isSubmit.value = true
                } catch (e: Throwable) {
                    e.printStackTrace()
                    if (e is ResultException) {
                        ToastUtils.showShort(e.msg)
                    } else {
                        ToastUtils.showShort("服务器异常，请稍后再试")
                    }
                }
            }
        } else {
            ToastUtils.showShort("请填写所有项目")
        }
    }

}
