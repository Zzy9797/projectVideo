package com.kiyotaka.jetpackdemo.ui.fragment.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kiyotaka.jetpackdemo.R
import com.kiyotaka.jetpackdemo.databinding.FragmentAboutBinding
import com.kiyotaka.jetpackdemo.databinding.FragmentSettingBinding
import com.kiyotaka.jetpackdemo.model.CustomViewModelProvider
import com.kiyotaka.jetpackdemo.model.SettingViewModel
import com.kiyotaka.jetpackdemo.ui.activity.MainActivity

class SettingFragment : Fragment() {

    private  val viewModel: SettingViewModel by viewModels {
        CustomViewModelProvider.providerSettingModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSettingBinding = DataBindingUtil.inflate(
            inflater
            , R.layout.fragment_setting
            , container
            , false
        )
        initData(binding)
        onSubscribeUi(binding)
        return binding.root
    }

    private fun onSubscribeUi(binding: FragmentSettingBinding) {
        binding.tvSettingCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.model?.isShow?.observe(viewLifecycleOwner, Observer { boolean ->
            val navView: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
            navView.visibility = View.GONE
        })

        binding.layoutSettingApp.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment2_to_changeKeyFragment)
        }

        binding.layoutSettingRes.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment2_to_suggestionFragment)
        }
    }

    private fun initData(binding: FragmentSettingBinding) {
        MainActivity.isOnHome = false

        binding.lifecycleOwner = this
        binding.model = viewModel
    }
}