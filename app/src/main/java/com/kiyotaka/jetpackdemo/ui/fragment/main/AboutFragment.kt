package com.kiyotaka.jetpackdemo.ui.fragment.main

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.kiyotaka.jetpackdemo.R
import com.kiyotaka.jetpackdemo.common.BaseApplication
import com.kiyotaka.jetpackdemo.databinding.FragmentAboutBinding
import com.kiyotaka.jetpackdemo.download.DownTask
import com.kiyotaka.jetpackdemo.download.DownloadListener
import com.kiyotaka.jetpackdemo.http.APIs
import com.kiyotaka.jetpackdemo.http.BaseBean
import com.kiyotaka.jetpackdemo.http.PostVersion
import com.kiyotaka.jetpackdemo.http.VersionResponse
import com.kiyotaka.jetpackdemo.model.AboutViewModel
import com.kiyotaka.jetpackdemo.model.CustomViewModelProvider
import com.kiyotaka.jetpackdemo.ui.activity.MainActivity
import com.mylhyl.circledialog.CircleDialog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class AboutFragment : Fragment() {

    private val pDialog: SweetAlertDialog by lazy {
        SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("加载数据中...")
    }
    private val viewModel: AboutViewModel by viewModels {
        CustomViewModelProvider.providerAboutModel()
    }
    private var downTask: DownTask? = null
    private lateinit var pd : ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentAboutBinding = DataBindingUtil.inflate(
            inflater
            , R.layout.fragment_about
            , container
            , false
        )
        initData(binding)
        onSubscribeUi(binding)
        return binding.root
    }

    private fun onSubscribeUi(binding: FragmentAboutBinding) {
        binding.tvAboutCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.model?.isShow?.observe(viewLifecycleOwner, Observer { boolean ->
            val navView: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
            navView.visibility = View.GONE
        })

        binding.layoutAboutApp.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_appFragment)
        }

        binding.layoutAboutPrivacy.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_secretFragment)
        }

        binding.layoutAboutVersion.setOnClickListener {
            checkVersion()
        }
    }

    private fun checkVersion() {
        pDialog.show()
        val client = Retrofit.Builder()
            .baseUrl("https://api.gdbrainview.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val request = client.create(APIs::class.java)
        val postVersion = PostVersion(getVersion(BaseApplication.context))
        val gson = Gson()
        val json = gson.toJson(postVersion)
        val body: RequestBody =
            RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json)
        val observe: Observable<BaseBean<ArrayList<VersionResponse>>> = request.postVersionCheck(body)
        observe.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :
                io.reactivex.Observer<BaseBean<ArrayList<VersionResponse>>> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: BaseBean<ArrayList<VersionResponse>>) {
                    pDialog.dismiss()
                    if (t.code == 200) {
                        val builder = CircleDialog.Builder()
                        builder.setTitle("是否升级到最新版本？").setText("${t.data[0].desc}")
                            .setNegative("否") {
                                builder.dismiss()
                            }.setPositive("是") {
                                builder.dismiss()
                                update("https://${t.data[0].apk_url}")
                            }.show(fragmentManager)
                    } else {
                        ToastUtils.showShort("当前已经是最新版本!")
                    }
                }

                override fun onError(e: Throwable) {
                    ToastUtils.showShort("${e.message}")
                    pDialog.dismiss()
                }

            })
    }

    private fun getVersion(context: Context): Int {
        var version = 0;
        try {
            version = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return version
    }

    private fun update(url: String) {
        downTask = DownTask(object : DownloadListener {
            override fun onProgress(process: Int) {
                pd.progress = process
            }
            override fun onSuccess() {
                downTask = null
                pd.dismiss()
                val fileName = url.substring(url.lastIndexOf("/"))
                val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
                val file = "$directory$fileName"
                openAPK(file)
            }
            override fun onFailed() {
                downTask = null
            }
            override fun onPaused() {
                downTask = null
            }
            override fun onCanceled() {
                downTask = null
            }
        })
        downTask?.execute(url)
        showProgress(url)
    }

    private fun showProgress(url : String?){
        pd = ProgressDialog(activity).apply {
            setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            setCancelable(true)
            setCanceledOnTouchOutside(false)
            setIcon(R.mipmap.ic_launcher)
            setTitle("下载中")
            max = 100
            setButton(DialogInterface.BUTTON_NEGATIVE,"取消",object :DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    if(downTask != null){
                        downTask?.cancelDownload()
                    }
                    if(url != null){
                        val fileName = url.substring(url.lastIndexOf("/"))
                        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
                        val file = File("$directory$fileName")
                        if(file.exists()){
                            file.delete()
                        }
                    }
                    pd.dismiss()
                }

            })
        }
        pd.show()
    }

    private fun openAPK(path: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val apkFile = File(path)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                val contentUri: Uri = FileProvider.getUriForFile(
                    BaseApplication.context,
                    "${BaseApplication.context.packageName}.provider",
                    apkFile
                )
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val hasInstallPermission: Boolean =
                        activity?.packageManager?.canRequestPackageInstalls()!!
                    if (!hasInstallPermission) {
                        startInstallPermissionSettingActivity()
                    }
                }
            } else {
                intent.setDataAndType(
                    Uri.fromFile(apkFile),
                    "application/vnd.android.package-archive"
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            if (activity?.packageManager?.queryIntentActivities(intent, 0)?.size!! > 0) {
                this.startActivity(intent)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun startInstallPermissionSettingActivity() {
        val packUri = Uri.parse("package:${activity?.packageName}")
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,packUri)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        activity?.startActivity(intent)
    }

    private fun initData(binding: FragmentAboutBinding) {
        MainActivity.isOnHome = false

        binding.lifecycleOwner = this
        binding.model = viewModel
    }

}