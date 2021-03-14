package com.kiyotaka.jetpackdemo.ui.fragment.main

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kiyotaka.jetpackdemo.R
import com.kiyotaka.jetpackdemo.adapter.ConsultAdapter
import com.kiyotaka.jetpackdemo.databinding.FragmentHomeBinding
import com.kiyotaka.jetpackdemo.model.CustomViewModelProvider
import com.kiyotaka.jetpackdemo.model.HomeViewModel
import com.kiyotaka.jetpackdemo.ui.activity.MainActivity
import com.mylhyl.circledialog.CircleDialog


class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels {
        CustomViewModelProvider.providerHomeModel(requireContext())
    }
    private val adapter = ConsultAdapter(this@HomeFragment)
    private val items = arrayOf("离线", "语音在线","视频在线","全部在线")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentHomeBinding = DataBindingUtil.inflate(
            inflater
            , R.layout.fragment_home
            , container
            , false
        )
        initData(binding)
        onSubscribeUi(binding)
        when(MainActivity.onlineStatus){
            1 -> binding.toolbar2.menu.findItem(R.id.home_line).setIcon(R.drawable.ic_online_24dp)
            0 -> binding.toolbar2.menu.findItem(R.id.home_line).setIcon(R.drawable.ic_offline_24dp)
            2 -> binding.toolbar2.menu.findItem(R.id.home_line).setIcon(R.drawable.ic_online_24dp)
            3 -> binding.toolbar2.menu.findItem(R.id.home_line).setIcon(R.drawable.ic_online_24dp)
        }
        return binding.root
    }

    private fun initData(binding: FragmentHomeBinding) {
        MainActivity.isOnHome = true

        binding.lifecycleOwner = this
        binding.model = homeViewModel

        binding.model?.queryConsults()
        binding.model?.queryCheck()

        binding.homeRecy.layoutManager = LinearLayoutManager(activity)
        binding.homeRecy.adapter = adapter

        binding.model?.queryStaticday()
        binding.model?.queryStaticweek()
    }

    private fun onSubscribeUi(binding: FragmentHomeBinding) {

        binding.model?.isShow?.observe(viewLifecycleOwner, Observer<Boolean> { boolean ->
            val navView: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
            navView.visibility = View.VISIBLE
        })

        binding.model?.isQuery?.observe(viewLifecycleOwner, Observer { boolean ->
            if (boolean) {
                adapter.submitList(binding.model?.dataList?.value)
                adapter.notifyDataSetChanged()
            }
        })

        binding.navigation1.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_home_to_recordFragment)
        }

        binding.navigation2.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_home_to_countFragment)
        }

        binding.model?.isCheck?.observe(viewLifecycleOwner, Observer { boolean ->
            if (boolean) {
                binding.tvHomeTips.visibility = View.VISIBLE
                binding.model?.isCheck?.value = false
                when (binding.model?.checkResultCode?.value) {
                    0 -> {
                        binding.model?.checkResult?.value = "您的资料还在审核中，暂时不能接受咨询"
                    }
                    1 -> {
                        binding.model?.checkResult?.value = "恭喜您，您的资料已审核通过！"
                    }

                    -1 -> {
                        binding.model?.checkResult?.value = "很遗憾，您暂时未提交资料或资料审核不通过！"
                        try {
                            val builder = CircleDialog.Builder()
                            builder.setTitle("提示")
                            builder.setText("您的资料未提交或审核不通过，前往个人资料提交资料证明")
                            builder.setPositive("前往") {
                                builder.dismiss()
                                val bundle = Bundle()
                                bundle.putBoolean("center",false)
                                findNavController().navigate(R.id.action_navigation_home_to_completeDataFragment,bundle)
                            }
                            builder.setNegative("取消", View.OnClickListener {
                                builder.dismiss()
                            })
                            builder.show(fragmentManager)

                        }catch (e: IllegalArgumentException){
                            e.printStackTrace()
                        }
                    }
                }
            }
        })

        binding.toolbar2.inflateMenu(R.menu.home_menu)
        binding.toolbar2.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.home_line -> {
                    val dialog: AlertDialog = AlertDialog
                        .Builder(requireContext())
                        .setTitle("状态")
                        .setIcon(R.drawable.ic_baseline_notifications_off_24)
                        .setSingleChoiceItems(items, MainActivity.onlineStatus, object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, which: Int) {
                                when (which) {
                                    0 -> {
                                        binding.model?.isOnline?.value = false
                                        binding.model?.isOnline(0)
                                        MainActivity.onlineStatus = 0
                                        binding.toolbar2.menu.findItem(R.id.home_line).setIcon(R.drawable.ic_offline_24dp)
                                    }
                                    1 -> {
                                        binding.model?.isOnline?.value = true
                                        binding.model?.isOnline(1)
                                        MainActivity.onlineStatus = 1
                                        binding.toolbar2.menu.findItem(R.id.home_line).setIcon(R.drawable.ic_online_24dp)
                                    }
                                    2 -> {
                                        binding.model?.isOnline?.value = true
                                        binding.model?.isOnline(2)
                                        MainActivity.onlineStatus = 2
                                        binding.toolbar2.menu.findItem(R.id.home_line).setIcon(R.drawable.ic_online_24dp)
                                    }
                                    3 -> {
                                        binding.model?.isOnline?.value = true
                                        binding.model?.isOnline(3)
                                        MainActivity.onlineStatus = 3
                                        binding.toolbar2.menu.findItem(R.id.home_line).setIcon(R.drawable.ic_online_24dp)
                                    }
                                }
                                dialog.dismiss()
                            }
                        }).create()
                    dialog.show()
                    return@setOnMenuItemClickListener true
                }

                else -> return@setOnMenuItemClickListener true
            }
        }
    }

}