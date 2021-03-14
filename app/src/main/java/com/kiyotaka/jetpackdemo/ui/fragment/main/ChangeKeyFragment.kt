package com.kiyotaka.jetpackdemo.ui.fragment.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer

import com.kiyotaka.jetpackdemo.R
import com.kiyotaka.jetpackdemo.databinding.FragmentChangeKeyBinding
import com.kiyotaka.jetpackdemo.model.ChangeKeyViewModel
import com.kiyotaka.jetpackdemo.model.CustomViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kiyotaka.jetpackdemo.ui.activity.MainActivity


class ChangeKeyFragment : Fragment() {

    private val changeKeyViewModel: ChangeKeyViewModel by viewModels {
        CustomViewModelProvider.providerChangeKeyModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentChangeKeyBinding = DataBindingUtil.inflate(
            inflater
            , R.layout.fragment_change_key
            , container
            , false
        )
        initData(binding)
        onSubscribeUi(binding)
        return binding.root
    }

    private fun initData(binding: FragmentChangeKeyBinding) {
        MainActivity.isOnHome = false

        binding.lifecycleOwner = this
        binding.model = changeKeyViewModel
    }

    private fun onSubscribeUi(binding: FragmentChangeKeyBinding){
        binding.tvChangeCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.model?.isShow?.observe(viewLifecycleOwner, Observer { boolean ->
            val navView: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
            navView.visibility = View.GONE
        })

        binding.model?.isSubmit?.observe(viewLifecycleOwner, Observer<Boolean> { boolean ->
            if(boolean){
                findNavController().popBackStack()
            }
        })
    }
}
