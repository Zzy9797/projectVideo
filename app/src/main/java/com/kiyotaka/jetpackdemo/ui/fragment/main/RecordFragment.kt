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
import com.kiyotaka.jetpackdemo.adapter.ConsultAdapter
import com.kiyotaka.jetpackdemo.databinding.FragmentRecordBinding
import com.kiyotaka.jetpackdemo.model.CustomViewModelProvider
import com.kiyotaka.jetpackdemo.model.RecordViewModel
import com.kiyotaka.jetpackdemo.ui.activity.MainActivity

class RecordFragment : Fragment() {

    private val recordViewModel: RecordViewModel by viewModels {
        CustomViewModelProvider.providerRecordModel(requireContext())
    }
    private val adapter =ConsultAdapter(this@RecordFragment)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentRecordBinding = DataBindingUtil.inflate(
            inflater
            , R.layout.fragment_record
            , container
            , false
        )
        initData(binding)
        onSubscribeUi(binding)
        return binding.root
    }

    private fun initData(binding: FragmentRecordBinding){
        MainActivity.isOnHome = false

        binding.lifecycleOwner = this
        binding.model = recordViewModel
        binding.model?.queryConsults()

        binding.recordRecy.layoutManager = LinearLayoutManager(activity)
        binding.recordRecy.adapter = adapter
    }

    private fun onSubscribeUi(binding: FragmentRecordBinding){
        binding.tvRecordCancel.setOnClickListener {
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

}
