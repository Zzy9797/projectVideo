package com.kiyotaka.jetpackdemo.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.KeyEvent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.blankj.utilcode.util.CollectionUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.kiyotaka.jetpackdemo.R
import com.kiyotaka.jetpackdemo.common.BaseApplication
import com.kiyotaka.jetpackdemo.http.*
import com.kiyotaka.jetpackdemo.media.BasePlayer
import com.kiyotaka.jetpackdemo.tencentvideo.ITRTCVideoCall
import com.kiyotaka.jetpackdemo.tencentvideo.TRTCVideoCallImpl
import com.kiyotaka.jetpackdemo.tencentvideo.TRTCVideoCallListener
import com.kiyotaka.jetpackdemo.tencentvideo.common.ProfileManager
import com.kiyotaka.jetpackdemo.tencentvideo.common.UserModel
import com.kiyotaka.jetpackdemo.tencentvideo.ui.TRTCVideoCallActivity
import com.tencent.imsdk.TIMCallBack
import com.tencent.imsdk.TIMLogLevel
import com.tencent.imsdk.TIMManager
import com.tencent.imsdk.TIMSdkConfig
import com.tencent.imsdk.session.SessionWrapper
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.jvm.functions.FunctionN


class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var music: String
        lateinit var phone: String
        var isOnHome = true
        var onlineStatus = 0
    }

    private val permissions = Arrays.asList(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )
    private var permissionRequestCount: Int = 0
    private val KEY_PERMISSIONS_REQUEST_COUNT = "KEY_PERMISSIONS_REQUEST_COUNT"
    private val MAX_NUMBER_REQUEST_PERMISSIONS = 4
    private val REQUEST_CODE_PERMISSIONS = 101
    private var firstTime = 0L

    private val mediaPlayer: BasePlayer = BasePlayer()

    private val pDialog: SweetAlertDialog by lazy {
        SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("初始化中...")
    }


    val sCall = TRTCVideoCallImpl.sharedInstance(BaseApplication.context)
    private val mTRTCVideoCallListener = object : TRTCVideoCallListener {
        override fun onNoResp(userId: String?) {
            Log.e("收到邀请", "onNoResp")
            //stopPlay()

        }

        override fun onUserLeave(userId: String?) {
            Log.e("收到邀请", "onUserLeave")
            //stopPlay()

        }

        override fun onUserVideoAvailable(userId: String?, isVideoAvailable: Boolean) {
            Log.e("收到邀请", "onUserVideoAvailable")
            //stopPlay()

        }

        override fun onCallingCancel() {
            Log.e("收到邀请", "onCallingCancel")
            //stopPlay()

        }

        override fun onGroupCallInviteeListUpdate(userIdList: MutableList<String>?) {
            Log.e("收到邀请", "onGroupCallInviteeListUpdate")
            //stopPlay()

        }

        override fun onReject(userId: String?) {
            Log.e("收到邀请", "onReject")
            //stopPlay()


        }

        override fun onCallEnd() {
            Log.e("收到邀请", "onCallEnd")
            //stopPlay()


        }

        override fun onLineBusy(userId: String?) {
            Log.e("收到邀请", "onLineBusy")
            //stopPlay()

        }

        override fun onInvited(
            sponsor: String?,
            userIdList: MutableList<String>?,
            isFromGroup: Boolean,
            callType: Int
        ) {
            //1. 收到邀请，先到服务器查询
            //startPlay()
            Log.e("收到邀请", "收到邀请")
            ProfileManager.getInstance()
                .getUserInfoByUserId(sponsor, object : ProfileManager.GetUserInfoCallback {
                    override fun onSuccess(model: UserModel?) {
                        Log.e("接受成功", "接受成功")
                        if (callType == TRTCVideoCallImpl.CallModel.CALL_TYPE_AUDIO) {
                            sCall.closeCamera()
                        }
                        if (!CollectionUtils.isEmpty(userIdList)) {
                            ProfileManager.getInstance().getUserInfoBatch(
                                userIdList,
                                object : ProfileManager.GetUserInfoBatchCallback {
                                    override fun onSuccess(modelList: List<UserModel?>?) {
                                        TRTCVideoCallActivity.startBeingCall(
                                            this@MainActivity,
                                            model,
                                            modelList,
                                            callType
                                        )

                                    }

                                    override fun onFailed(code: Int, msg: String?) {
                                        TRTCVideoCallActivity.startBeingCall(
                                            this@MainActivity,
                                            model,
                                            null,
                                            callType
                                        )
                                    }
                                })
                        } else {
                            TRTCVideoCallActivity.startBeingCall(
                                this@MainActivity,
                                model,
                                null,
                                callType
                            )
                        }
                    }

                    override fun onFailed(code: Int, msg: String?) {
                        Log.e("接受失败", code.toString() + msg)
                        //          stopPlay()
                    }
                })
        }

        override fun onCallingTimeout() {
            Log.e("收到邀请", "onCallingTimeout")
            //stopPlay()

        }

        override fun onUserEnter(userId: String?) {
            Log.e("收到邀请", "onUserEnter")
            //stopPlay()
            sCall.sendEnterModel()
        }

        override fun onUserAudioAvailable(userId: String?, isVideoAvailable: Boolean) {
            Log.e("收到邀请", "onUserAudioAvailable")
            //stopPlay()

        }

        override fun onUserVoiceVolume(volumeMap: MutableMap<String, Int>?) {
            Log.e("收到邀请", "onUserVoiceVolume")
            // stopPlay()

        }

        override fun onError(code: Int, msg: String?) {
            Log.e("收到邀请", "onError")
            //stopPlay()

        }

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.setCancelable(false)
        pDialog.show()





        phone = intent.getStringExtra("phone")!!

        val navView: BottomNavigationView = findViewById(R.id.nav_view)


        savedInstanceState?.let {
            permissionRequestCount = it.getInt(KEY_PERMISSIONS_REQUEST_COUNT, 0)
        }

        requestPermissionsIfNecessary()

        getSig()
        queryMusic()
        offline()
        checkin()

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)


    }

    private fun requestPermissionsIfNecessary() {
        if (!checkAllPermissions()) {
            if (permissionRequestCount < MAX_NUMBER_REQUEST_PERMISSIONS) {
                permissionRequestCount += 1
                ActivityCompat.requestPermissions(
                    this,
                    permissions.toTypedArray(),
                    REQUEST_CODE_PERMISSIONS
                )
            } else {
                ToastUtils.showShort("app运行需要申请权限，请点击授权")
            }
        }
    }

    private fun checkAllPermissions(): Boolean {
        var hasPermissions = true
        for (permission in permissions) {
            hasPermissions = hasPermissions and isPermissionGranted(permission)
        }
        return hasPermissions
    }

    private fun isPermissionGranted(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            requestPermissionsIfNecessary() // no-op if permissions are granted already.
        }
    }

    /**
     * 登录腾讯云
     */
    private fun loginYun(sig: String) {
        ProfileManager.getInstance().login(sig, phone, "", object : ProfileManager.ActionCallback {
            override fun onSuccess() {
                //   ToastUtils.showLong("登录成功")
            }

            override fun onFailed(code: Int, msg: String?) {
                //   ToastUtils.showLong("登录失败")
            }
        })

        // 由于两个模块公用一个IM，所以需要在这里先进行登录，您的App只使用一个model，可以直接调用VideoCall 对应函数
        // 目前 Demo 为了方便您接入，使用的是本地签发 sig 的方式，您的项目上线，务必要保证将签发逻辑转移到服务端，否者会出现 key 被盗用，流量盗用的风险。
        if (SessionWrapper.isMainProcess(this)) {
            val config = TIMSdkConfig(1400374764)
                .enableLogPrint(true)
                .setLogLevel(TIMLogLevel.DEBUG)
                .setLogPath(
                    Environment.getExternalStorageDirectory().path + "/justfortest/"
                )
            //初始化 SDK
            TIMManager.getInstance().init(this, config)
        }
        val userId = ProfileManager.getInstance().userModel.userId
        val userSig = ProfileManager.getInstance().userModel.userSig
        Log.e("Login", "login: $userId $userSig")
        // 由于这里提前登陆了，所以会导致上一次的消息收不到，您在APP中单独使用 ITRTCAudioCall.login 不会出现这种问题
        TIMManager.getInstance().login(userId, userSig, object : TIMCallBack {
            override fun onError(i: Int, s: String) {
                // 登录IM失败
                ToastUtils.showLong("登录IM失败，所有功能不可用[$i]$s")
                pDialog.dismiss()
                Log.e("login", "登录IM失败，所有功能不可用[$i]$s")
            }

            override fun onSuccess() {
                //1. 登录IM成功
                ToastUtils.showLong("登录IM成功")
                initVideoCallData()
            }
        })
    }

    private fun initVideoCallData() {
        sCall.init()
        sCall.addListener(mTRTCVideoCallListener)
        val appid: Int = 1400374764
        val userId = ProfileManager.getInstance().userModel.userId
        val userSig = ProfileManager.getInstance().userModel.userSig
        sCall.login(appid
            , userId
            , userSig
            , object : ITRTCVideoCall.ActionCallBack {
                override fun onSuccess() {
                    //4. 此处为实例代码：我们在组件登录成功后即打开摄像头并呼叫用户“aaa”
                    ToastUtils.showShort("初始化成功")
                }

                override fun onError(code: Int, msg: String?) {
                    ToastUtils.showShort("初始化失败")
                    Log.e("视频", code.toString() + msg)
                }
            })
        pDialog.dismiss()

    }

    /**
     * 初始化播放器
     */
    private fun initMediaPlayer() {
        mediaPlayer.setVolume(0.5f, 0.5f)
        mediaPlayer.isLooping = false
    }

    /**
     * 播放
     */
    private fun startPlay() {
        if (music != "") {
            try {
                mediaPlayer.setVolume(0.5f, 0.5f)
                mediaPlayer.isLooping = false
                mediaPlayer.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 停止
     */
    private fun stopPlay() {
        if (mediaPlayer == null) {
            return
        }
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    /**
     * 销毁app离线
     */
    private fun offline() {
        val client = OkHttpClient()
        val onLine = OnLine(
            phone.toLong(),
            0
        )
        val gson = Gson()
        val json = gson.toJson(onLine)
        val body: RequestBody =
            RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json)
        val request = Request.Builder()
            .url("https://api.gdbrainview.com/api/v1/app/user/online")
            .post(body)
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                ToastUtils.showShort("网络出错了")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.e("offline", response.code.toString())
            }

        })
    }

    /**
     * 获取密钥
     */
    private fun getSig() {
        val client = OkHttpClient()
        val postYun = PostYun(phone)
        val gson = Gson()
        val json = gson.toJson(postYun)
        val body: RequestBody =
            RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json)
        val request = Request.Builder()
            .url("https://api.gdbrainview.com/api/v1/consult/txyun")//"http://120.78.82.117:8686/api/v1/consult/txyun")
            .post(body)
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                ToastUtils.showShort("网络出错了")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.e("yun", response.code.toString())
                if (response.code == 200) {
                    val result = response.body?.string()
                    Log.e("yun", result)
                    val gson = Gson()
                    val data = gson.fromJson(result, YunResponse::class.java)
                    val userSig = data.data

                    runOnUiThread {
                        loginYun(userSig)
                    }
                }
            }

        })
    }

    fun queryMusic() {
        val client = OkHttpClient()
        val postMedia = PostMedia(phone.toLong())
        val gson = Gson()
        val json = gson.toJson(postMedia)
        val body: RequestBody =
            RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json)
        val request = Request.Builder()
            .url("https://api.gdbrainview.com/api/v1/app/user/downmp3")//"http://120.78.82.117:8686/api/v1/consult/txyun")
            .post(body)
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                ToastUtils.showShort("网络出错了")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 200) {
                    val result = response.body?.string()
                    Log.e("yun", result)
                    val gson = Gson()
                    val listMedia = gson.fromJson(result, ListMedia::class.java)
                    music = if (listMedia.data[0].status) {
                        "https://${listMedia.data[0].mp3}"
                    } else {
                        ""
                    }
                    //mediaPlayer.setDataSource(music)
                    //mediaPlayer.prepareAsync()


                }
            }

        })
    }

    /**
     * 轮询 暂时一分钟轮询一次
     */
    private fun checkin() {
        val client = Retrofit.Builder()
            .baseUrl("https://api.gdbrainview.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val request = client.create(APIs::class.java)
        val postMedia = PostMedia(phone.toLong())
        val gson = Gson()
        val json = gson.toJson(postMedia)
        val body: RequestBody =
            RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json)
        val observe: Observable<Any> = request.postCheckOnline(body)
        observe.repeatWhen {
            it.delay(60, TimeUnit.SECONDS)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
            Log.e("checkin", it.toString())
        }
    }

    override fun onBackPressed() {
        if (isOnHome) {
            if (System.currentTimeMillis() - firstTime > 2000) {
                ToastUtils.showShort("再按一次后退键退出程序")
                firstTime = System.currentTimeMillis()
            } else {
                offline()
                val setIntent = Intent(Intent.ACTION_MAIN)
                setIntent.addCategory(Intent.CATEGORY_HOME)
                setIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(setIntent)
                //this.finish();
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // 是否触发按键为back键
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed()
            //   mediaPlayer.stop()
            //  mediaPlayer.release()
            true
        } else { // 如果不是back键正常响应
            super.onKeyDown(keyCode, event)
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        offline()
        sCall.removeListener(mTRTCVideoCallListener)
        TRTCVideoCallImpl.destroySharedInstance()
    }
}

