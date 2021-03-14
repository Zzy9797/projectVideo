package com.kiyotaka.jetpackdemo.ui.fragment.login


import android.database.Observable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.kiyotaka.jetpackdemo.common.BaseApplication
import com.kiyotaka.jetpackdemo.R
import com.kiyotaka.jetpackdemo.databinding.FragmentRegisterBinding
import com.kiyotaka.jetpackdemo.model.CustomViewModelProvider
import com.kiyotaka.jetpackdemo.model.RegisterViewModel

/**
 * A simple [Fragment] subclass.
 */
class RegisterFragment : Fragment() {

    private val registerModel: RegisterViewModel by viewModels {
        CustomViewModelProvider.providerRegisterModel(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentRegisterBinding = DataBindingUtil.inflate(
            inflater
            , R.layout.fragment_register
            , container
            , false
        )
        initData(binding)
        onSubscribeUi(binding)
        return binding.root
    }

    private fun initData(binding: FragmentRegisterBinding) {
        binding.lifecycleOwner = this
        binding.registerModel = registerModel
    }

    private fun onSubscribeUi(binding: FragmentRegisterBinding) {
        binding.tvRegisterCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        if (binding.registerModel?.isRegister?.value!!) {
            findNavController().popBackStack()
        }

        binding.registerModel?.isRegister?.observe(viewLifecycleOwner, Observer<Boolean> { boolean ->
            if(boolean){
                findNavController().popBackStack()
            }
        })
    }

}
