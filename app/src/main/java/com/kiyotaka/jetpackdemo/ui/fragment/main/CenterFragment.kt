package com.kiyotaka.jetpackdemo.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.kiyotaka.jetpackdemo.R
import com.kiyotaka.jetpackdemo.adapter.CenterAdapter
import com.kiyotaka.jetpackdemo.databinding.FragmentCenterBinding
import com.kiyotaka.jetpackdemo.generated.callback.OnClickListener
import com.kiyotaka.jetpackdemo.http.OnLine
import com.kiyotaka.jetpackdemo.model.CenterViewModel
import com.kiyotaka.jetpackdemo.model.CustomViewModelProvider
import com.kiyotaka.jetpackdemo.ui.activity.MainActivity
import com.mylhyl.circledialog.CircleDialog
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.nio.BufferUnderflowException


class CenterFragment : Fragment() {

    private val centerViewModel: CenterViewModel by viewModels {
        CustomViewModelProvider.providerCenterModel(requireContext())
    }
   // val list = listOf<String>("个人资料", "咨询记录查询", "数据统计", "咨询师审核", "修改密码","退出")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentCenterBinding = DataBindingUtil.inflate(
            inflater
            , R.layout.fragment_center
            , container
            , false
        )
        initData(binding)
        onSubscribeUi(binding)
        return binding.root
    }

    private fun initData(binding: FragmentCenterBinding) {
        MainActivity.isOnHome = true
        binding.lifecycleOwner = this
        binding.model = centerViewModel
       /* val adapter = CenterAdapter(this@CenterFragment)
        adapter.submitList(list)
        binding.centerRecy.layoutManager = LinearLayoutManager(activity)
        binding.centerRecy.adapter = adapter

        */

        binding.model?.queryBash()
    }

    private fun onSubscribeUi(binding: FragmentCenterBinding) {
        binding.model?.isShow?.observe(viewLifecycleOwner, Observer<Boolean> { boolean ->
            val navView: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
            navView.visibility = View.VISIBLE
        })

        binding.model?.isQuery?.observe(viewLifecycleOwner, Observer { boolean ->
            if(boolean) {
                Glide.with(this@CenterFragment).load(binding.model?.imageUrl?.value).apply(
                    RequestOptions.bitmapTransform(
                        CircleCrop()
                    )
                ).into(binding.ivHead)
            }
        })

        binding.LinearLayoutcenter2.setOnClickListener{
            val bundle = Bundle()
            bundle.putBoolean("center",true)
            findNavController().navigate(R.id.action_navigation_person_to_completeDataFragment,bundle)
        }
        binding.LinearLayoutcenter3.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_person_to_checkFragment)
        }

        binding.LinearLayoutcenter6.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_person_to_settingFragment)
        }
        binding.LinearLayoutcenter5.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_person_to_settingFragment2)
        }

        binding.LinearLayoutcenter7.setOnClickListener {
            val builder = CircleDialog.Builder()
            builder.setTitle("确定是否退出程序？")
            builder.setPositive("确定") {
                offline()
                activity?.finish()
            }
            builder.setNegative("取消"){

            }
            builder.show(fragmentManager)
        }
    }

    /**
     * 销毁app离线
     */
    private fun offline() {
        val client = OkHttpClient()
        val onLine = OnLine(
            MainActivity.phone.toLong(),
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
            }

        })
    }
}