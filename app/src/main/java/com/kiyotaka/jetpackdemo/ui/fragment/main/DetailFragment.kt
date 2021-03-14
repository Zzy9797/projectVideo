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
import com.kiyotaka.jetpackdemo.databinding.FragmentDetailBinding
import com.kiyotaka.jetpackdemo.model.CustomViewModelProvider
import com.kiyotaka.jetpackdemo.model.DetailViewModel
import com.kiyotaka.jetpackdemo.ui.activity.MainActivity

class DetailFragment : Fragment() {

    private val detailViewModel: DetailViewModel by viewModels {
        CustomViewModelProvider.providerDetailModel(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentDetailBinding = DataBindingUtil.inflate(
            inflater
            , R.layout.fragment_detail
            , container
            , false
        )
        initData(binding)
        onSubscribeUi(binding)
        return binding.root
    }

    private fun initData(binding: FragmentDetailBinding) {
        MainActivity.isOnHome = false

        binding.lifecycleOwner = this
        binding.model = detailViewModel

        val id = arguments?.getInt("id")
        binding.model?.id?.value = id.toString()
        binding.model?.queryDetail(id!!)
    }

    private fun onSubscribeUi(binding: FragmentDetailBinding) {

        binding.model?.isShow?.observe(viewLifecycleOwner, Observer<Boolean> { boolean ->
            val navView: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
            navView.visibility = View.GONE
        })

        binding.tvDetailCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}
