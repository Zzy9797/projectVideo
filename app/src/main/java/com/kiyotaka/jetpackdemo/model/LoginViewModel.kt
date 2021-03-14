package com.kiyotaka.jetpackdemo.model

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.kiyotaka.jetpackdemo.common.BaseApplication
import com.kiyotaka.jetpackdemo.db.repository.UserRepository
import com.kiyotaka.jetpackdemo.http.PostLogin
import com.kiyotaka.jetpackdemo.http.ResultException
import com.kiyotaka.jetpackdemo.http.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.regex.Matcher
import java.util.regex.Pattern


class LoginViewModel constructor(
    private val repository: UserRepository
) : ViewModel() {

    val account = MutableLiveData("")
    val password = MutableLiveData("")
    val enable = MutableLiveData(false)
    val isMobile = MutableLiveData(false)
    val isLogin = MutableLiveData(false)
    val isSave = MutableLiveData(false)
    val phone = MutableLiveData("")

    /**
     * 初始化
     */
    fun initData() {
        val sp = BaseApplication.context.getSharedPreferences("account", Context.MODE_PRIVATE)
        Log.e("initdata", sp.getBoolean("save", false).toString())

        if (sp.getBoolean("save", false)){
            account.value = sp.getString("account","")
            password.value = sp.getString("password","")
            isSave.value = true
            isMobile.value = isMobile(account.value)
            judgeEnable()
        }
    }

    /**
     * 账号
     */
    fun onAccountChanged(s: CharSequence) {
        account.value = s.toString()
        isMobile.value = isMobile(account.value)
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
     * 判断是否为空
     */
    private fun judgeEnable() {
        enable.value = account.value!!.isNotEmpty()
                && password.value!!.isNotEmpty()
    }

    /**
     * 记住密码
     */
    private fun savePassword() {
        val sp = BaseApplication.context.getSharedPreferences("account", Context.MODE_PRIVATE)
        val ed = sp.edit()
        ed.putBoolean("save", true)
        ed.putString("account", account.value!!)
        ed.putString("password", password.value!!)
        ed.apply()
    }

    /**
     * 不记住，清除掉sp
     */
    private fun clearPassword() {
        val sp = BaseApplication.context.getSharedPreferences("account", Context.MODE_PRIVATE)
        val ed = sp.edit()
        ed.clear()
        ed.apply()
        ed.putBoolean("save", false)
        ed.apply()
    }

    /**
     * 登录
     */
    fun login() {
        when {
            isMobile.value!! && enable.value!! -> {
                if (isSave.value!!) {
                    savePassword()
                } else {
                    clearPassword()
                }
                val postLogin = PostLogin(
                    account.value!!.toLong()
                    , password.value!!
                )
                val gson = Gson()
                val json = gson.toJson(postLogin)
                val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                viewModelScope.launch {
                    try {
                        val data = withContext(Dispatchers.IO) {
                            RetrofitClient.reqApi.login(body)
                        }
                        ToastUtils.showShort(data.msg)
                        if (data.code == 201 || data.code == 200) {
                            val response = data.data[0]
                            phone.value = response.phone
                            isLogin.value = true
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
            !enable.value!! -> {
                ToastUtils.showShort("请填写账号与密码")
            }

            !isMobile.value!! -> {
                ToastUtils.showShort("请填写正确的手机号码格式")
            }

        }
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