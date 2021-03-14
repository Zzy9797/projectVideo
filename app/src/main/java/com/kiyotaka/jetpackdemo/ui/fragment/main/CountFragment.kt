package com.kiyotaka.jetpackdemo.ui.fragment.main

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kiyotaka.jetpackdemo.R
import com.kiyotaka.jetpackdemo.databinding.FragmentCountBinding
import com.kiyotaka.jetpackdemo.model.CountViewModel
import com.kiyotaka.jetpackdemo.model.CustomViewModelProvider
import com.kiyotaka.jetpackdemo.ui.activity.MainActivity
import java.text.SimpleDateFormat
import java.util.*

class CountFragment : Fragment() {

    private val countViewModel: CountViewModel by viewModels {
        CustomViewModelProvider.providerCountModel()
    }
    private var str = ""
    lateinit var binding: FragmentCountBinding

    private val startListener: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            str = "$year-${month + 1}-$dayOfMonth"
            val sdate = "$str 00:00:00"
            Log.e("date", "$sdate ")
            binding.model?.startDate?.value = sdate
            binding.model?.showStartDate?.value = str
            ToastUtils.showShort("请选择结束日期")
        }
    private val endListener: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            str = "$year-${month + 1}-$dayOfMonth"
            val sdate = "$str 23:59:59"
            Log.e("date", "$sdate ")
            binding.model?.endDate?.value = sdate
            binding.model?.showEndDate?.value = str
            binding.model?.queryMonth(binding.model?.startDate?.value!!,sdate)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater
            , R.layout.fragment_count
            , container
            , false
        )
        initData()
        onSubscribeUi()
        return binding.root
    }

    private fun initData() {
        MainActivity.isOnHome = false

        binding.lifecycleOwner = this
        binding.model = countViewModel
        binding.model?.queryAll()
        binding.model?.initData()
        binding.detailLo.visibility = View.GONE
    }

    private fun onSubscribeUi() {

        binding.tvCountCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.model?.isShow?.observe(viewLifecycleOwner, Observer { boolean ->
            val navView: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
            navView.visibility = View.GONE
        })

        binding.txtCountStart.setOnClickListener {
            val dialog = DatePickerDialog(
                requireContext(),
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                startListener,
                binding.model?.pickerYear?.value!!,
                binding.model?.pickerMonth?.value!!,
                binding.model?.pickerDay?.value!!
            )
            dialog.show()
        }
        binding.txtCountEnd.setOnClickListener {
            val dialog = DatePickerDialog(
                requireContext(),
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                endListener,
                binding.model?.pickerYear?.value!!,
                binding.model?.pickerMonth?.value!!,
                binding.model?.pickerDay?.value!!
            )
            dialog.show()
        }
    }
}
