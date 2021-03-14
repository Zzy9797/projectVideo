package com.kiyotaka.jetpackdemo.ui.fragment.main

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.kiyotaka.jetpackdemo.R
import com.kiyotaka.jetpackdemo.http.APIs
import com.kiyotaka.jetpackdemo.http.AppContent
import com.kiyotaka.jetpackdemo.http.BaseBean
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_app.view.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_app, container, false)
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            view.tv_app_content.settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        }
        view.tv_app_content.settings.blockNetworkImage = false
        view.tv_app_cancel.setOnClickListener {
            findNavController().popBackStack()
        }
        queryAppContent(view)
        return view
    }

    private fun queryAppContent(v: View) {
        val client = Retrofit.Builder()
            .baseUrl("https://api.gdbrainview.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val observe: Observable<BaseBean<ArrayList<AppContent>>> =
            client.create(APIs::class.java).postFunctionApp()
        observe.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :
                Observer<BaseBean<ArrayList<AppContent>>> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: BaseBean<ArrayList<AppContent>>) {
                    v.tv_app_content.loadDataWithBaseURL(null,t.data[0]?.content,"text/html","UTF-8",null)
                }

                override fun onError(e: Throwable) {
                    ToastUtils.showShort("${e.message}")
                }

            })
    }

}