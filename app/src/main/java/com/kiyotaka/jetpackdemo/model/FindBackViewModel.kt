package com.kiyotaka.jetpackdemo.model

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.kiyotaka.jetpackdemo.common.BaseApplication
import com.kiyotaka.jetpackdemo.http.PostFindBack
import com.kiyotaka.jetpackdemo.http.PostSms
import com.kiyotaka.jetpackdemo.http.ResultException
import com.kiyotaka.jetpackdemo.http.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.Exception
import java.util.regex.Matcher
import java.util.regex.Pattern

class FindBackViewModel : ViewModel() {

    val account = MutableLiveData("")
    val smsCode = MutableLiveData("")
    val password = MutableLiveData("")
    val passwordAgain = MutableLiveData("")
    val isSame = MutableLiveData(false)
    val enable = MutableLiveData(false)
    val isMobile = MutableLiveData(false)
    val isFind = MutableLiveData(false)

    /**
     * 账号
     */
    fun onAccountChanged(s: CharSequence) {
        account.value = s.toString()
        isMobile.value = isMobile(account.value)
        judgeEnable()
    }

    /**
     * 获取验证码
     */
    fun onGetSmsCode() {
        val context = BaseApplication.context
        if (isMobile.value!!) {
            val postSms = PostSms(account.value!!.toLong())
            val gson = Gson()
            val json = gson.toJson(postSms)
            val body: RequestBody =
                json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            viewModelScope.launch {
                try {
                    val data = withContext(Dispatchers.IO) {
                        RetrofitClient.reqApi.getSmsCode(body)
                    }
                    ToastUtils.showShort(data.msg)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            Toast.makeText(context, "请输入正确的手机号码", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 验证码
     */
    fun onSmsCodeChanged(s: CharSequence) {
        smsCode.value = s.toString()
        judgeEnable()
    }

    /**
     * 密码
     */
    fun onPasswordChanged(s: CharSequence) {
        password.value = s.toString()
        judgeEnable()
    }

    /**
     * 确认密码
     */
    fun onPasswordChangedAgain(s: CharSequence) {
        passwordAgain.value = s.toString()
        judgeEnable()
        judgeSame()
    }

    /**
     * 找回
     */
    fun onSubmit() {
        when {
            isSame.value!! && enable.value!! && isMobile.value!! -> {
                val postFindBack = PostFindBack(
                    account.value!!.toLong()
                    , password.value!!
                    , smsCode.value!!.toInt()
                )
                val gson = Gson()
                val json = gson.toJson(postFindBack)
                val body: RequestBody =
                    json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                viewModelScope.launch {
                    try {
                        val data = withContext(Dispatchers.IO) {
                            RetrofitClient.reqApi.findBack(body)
                        }
                        ToastUtils.showShort(data.msg)
                        isFind.value = true
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

            !enable.value!! -> {
                ToastUtils.showShort("请输入所有必填项")
            }

            !isMobile.value!! -> {
                ToastUtils.showShort("请输入手机号码！")
            }
            !isSame.value!! -> {
                ToastUtils.showShort("前后两个密码不一致，请检查")
            }


        }
    }

    /**
     * 必填项是否都填了
     */
    private fun judgeEnable() {
        enable.value = account.value!!.isNotEmpty()
                && smsCode.value!!.isNotEmpty()
                && password.value!!.isNotEmpty()
                && passwordAgain.value!!.isNotEmpty()
    }

    /**
     * 两个密码是否一致
     */
    private fun judgeSame() {
        isSame.value = password.value!! == passwordAgain.value!!
    }

    private fun isMobile(str: String?): Boolean {
        val format = "^[1][3,4,5,7,8][0-9]{9}$"
        return if (str == null) false else isMatches(str, format)
    }

    private fun isMatches(text: String?, format: String): Boolean {
        val pattern: Pattern = Pattern.compile(format)
        val matcher: Matcher = pattern.matcher(text)
        return matcher.matches()
    }
}