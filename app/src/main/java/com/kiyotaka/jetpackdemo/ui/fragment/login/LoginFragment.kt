package com.kiyotaka.jetpackdemo.ui.fragment.login


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.kiyotaka.jetpackdemo.common.BaseApplication

import com.kiyotaka.jetpackdemo.R
import com.kiyotaka.jetpackdemo.databinding.FragmentLoginBinding
import com.kiyotaka.jetpackdemo.http.PostLogin
import com.kiyotaka.jetpackdemo.http.RetrofitClient
import com.kiyotaka.jetpackdemo.model.CustomViewModelProvider
import com.kiyotaka.jetpackdemo.model.LoginViewModel
import com.kiyotaka.jetpackdemo.ui.activity.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by viewModels {
        CustomViewModelProvider.providerLoginModel(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentLoginBinding = DataBindingUtil.inflate(
            inflater
            , R.layout.fragment_login
            , container
            , false
        )
        initData(binding)
        onSubscribeUi(binding)
        return binding.root
    }

    private fun initData(binding: FragmentLoginBinding) {
        binding.lifecycleOwner = this
        binding.loginModel = loginViewModel
        binding.loginModel?.initData()
        if (binding.loginModel?.isSave?.value!!) {
            binding.etLoginAccount.setText(binding.loginModel?.account?.value!!)
            binding.etLoginPwd.setText(binding.loginModel?.password?.value!!)
        }
    }

    private fun onSubscribeUi(binding: FragmentLoginBinding) {
        binding.txtLoginForget.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_findBackFragment)
        }

        binding.txtLoginRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        loginViewModel.isLogin.observe(viewLifecycleOwner, Observer<Boolean> { boolean ->
            if (boolean) {
                val intent = Intent(activity, MainActivity::class.java)
                intent.putExtra("phone", loginViewModel.phone.value)
                startActivity(intent)
                activity?.finish()
            }
        })
        binding.cbLoginSave.setOnCheckedChangeListener{ _, isChecked ->
            binding.loginModel?.isSave?.value = isChecked
        }

    }

}
