package com.kiyotaka.jetpackdemo.ui.fragment.login


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.kiyotaka.jetpackdemo.common.BaseApplication

import com.kiyotaka.jetpackdemo.R
import com.kiyotaka.jetpackdemo.databinding.FragmentFindBackBinding
import com.kiyotaka.jetpackdemo.http.PostFindBack
import com.kiyotaka.jetpackdemo.http.RetrofitClient
import com.kiyotaka.jetpackdemo.model.CustomViewModelProvider
import com.kiyotaka.jetpackdemo.model.FindBackViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * A simple [Fragment] subclass.
 */
class FindBackFragment : Fragment() {

    private val findBackViewModel: FindBackViewModel by viewModels {
        CustomViewModelProvider.providerFindBackModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentFindBackBinding = DataBindingUtil.inflate(
            inflater
            , R.layout.fragment_find_back
            , container
            , false
        )
        initData(binding)
        onSubscribeUi(binding)
        return binding.root
    }

    private fun initData(binding: FragmentFindBackBinding) {
        binding.lifecycleOwner = this
        binding.model = findBackViewModel
    }


    private fun onSubscribeUi(binding: FragmentFindBackBinding){
        binding.tvFindBackCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.model?.isFind?.observe(viewLifecycleOwner, Observer<Boolean> { boolean ->
            if(boolean){
                findNavController().popBackStack()
            }
        })
    }


}
