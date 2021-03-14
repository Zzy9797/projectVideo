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
import com.kiyotaka.jetpackdemo.databinding.FragmentVerifyBinding
import com.kiyotaka.jetpackdemo.model.CustomViewModelProvider
import com.kiyotaka.jetpackdemo.model.VerifyViewModel
import com.kiyotaka.jetpackdemo.ui.activity.MainActivity

class VerifyFragment : Fragment() {

    private val verifyViewModel: VerifyViewModel by viewModels {
        CustomViewModelProvider.providerVerifyModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentVerifyBinding = DataBindingUtil.inflate(
            inflater
            , R.layout.fragment_verify
            , container
            , false
        )
        initData(binding)
        onSubscribeUi(binding)
        return binding.root
    }

    private fun initData(binding: FragmentVerifyBinding) {
        MainActivity.isOnHome = false

        binding.lifecycleOwner = this
        binding.model = verifyViewModel
        binding.model?.queryRecord(requireArguments().getInt("id"))
    }

    private fun onSubscribeUi(binding: FragmentVerifyBinding){
        //binding.model?.queryStatus()

        binding.tvVerifyCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.model?.isShow?.observe(viewLifecycleOwner, Observer { boolean ->
            val navView: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
            navView.visibility = View.GONE
        })

    }


}
