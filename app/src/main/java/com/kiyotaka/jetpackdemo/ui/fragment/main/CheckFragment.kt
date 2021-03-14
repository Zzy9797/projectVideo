package com.kiyotaka.jetpackdemo.ui.fragment.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kiyotaka.jetpackdemo.R
import com.kiyotaka.jetpackdemo.adapter.CheckRecordAdapter
import com.kiyotaka.jetpackdemo.databinding.FragmentCheckBinding
import com.kiyotaka.jetpackdemo.databinding.FragmentRecordBinding
import com.kiyotaka.jetpackdemo.model.CheckViewModel
import com.kiyotaka.jetpackdemo.model.CustomViewModelProvider

class CheckFragment : Fragment() {

    private val checkViewModel:CheckViewModel by viewModels {
        CustomViewModelProvider.providerCheckRecordModel()
    }
    private val adapter = CheckRecordAdapter(this@CheckFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentCheckBinding = DataBindingUtil.inflate(
            inflater
            , R.layout.fragment_check
            , container
            , false
        )
        initData(binding)
        onSubscribeUi(binding)
        return binding.root
    }

    private fun onSubscribeUi(binding: FragmentCheckBinding) {
        binding.tvCheckCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.model?.isShow?.observe(viewLifecycleOwner, Observer { boolean ->
            val navView: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
            navView.visibility = View.GONE
        })

        binding.model?.isQuery?.observe(viewLifecycleOwner, Observer { boolean ->
            if(boolean){
                adapter.submitList(binding.model?.dataList?.value)
                adapter.notifyDataSetChanged()
                ToastUtils.showShort("加载完成")
            }
        })
    }

    private fun initData(binding: FragmentCheckBinding) {
        binding.lifecycleOwner = this
        binding.model = checkViewModel
        binding.model?.queryRecord()

        binding.checkRecy.layoutManager = LinearLayoutManager(activity)
        binding.checkRecy.adapter = adapter
    }
}