package com.kiyotaka.jetpackdemo.ui.fragment.main


import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableStringBuilder
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
import androidx.recyclerview.widget.GridLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kiyotaka.jetpackdemo.R
import com.kiyotaka.jetpackdemo.adapter.CheckAdapter
import com.kiyotaka.jetpackdemo.databinding.FragmentCompleteDataBinding
import com.kiyotaka.jetpackdemo.http.City
import com.kiyotaka.jetpackdemo.model.CustomViewModelProvider
import com.kiyotaka.jetpackdemo.model.DataViewModel
import com.kiyotaka.jetpackdemo.ui.activity.MainActivity
import com.mylhyl.circledialog.CircleDialog
import com.mylhyl.circledialog.params.DialogParams
import com.mylhyl.circledialog.params.ItemsParams
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class DataFragment : Fragment() {


    // 选择图片的标识
    private val REQUEST_CODE_IMAGE = 100
    private val REQUEST_CODE_FILE = 200
    private val REQUEST_CODE_FILE_IMAGE = 300

    private val pDialog: SweetAlertDialog by lazy {
        SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("加载数据中...")
    }

    private val listener: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            binding.model?.workYear?.value = year.toString()
        }


    private val dataViewModel: DataViewModel by viewModels {
        CustomViewModelProvider.providerDataModel(requireContext())
    }
    private lateinit var binding: FragmentCompleteDataBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater
            , R.layout.fragment_complete_data
            , container
            , false
        )
        initData(binding)
        onSubscribeUi(binding)
        return binding.root
    }

    private fun initData(binding: FragmentCompleteDataBinding) {
        MainActivity.isOnHome = false

        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.setCancelable(false)
        pDialog.show()

        binding.lifecycleOwner = this
        binding.model = dataViewModel
        binding.model?.initData(requireArguments().getBoolean("center",false))
        requireArguments().clear()
        binding.model?.getArea()
        binding.model?.getCity()
    }

    private fun onSubscribeUi(binding: FragmentCompleteDataBinding) {

        /** 加载SP数据*/
        binding.model?.isLoadFromSP?.observe(viewLifecycleOwner, Observer { boolean ->
            if (boolean) {

                pDialog.dismiss()
                binding.btnCompDataSave.visibility = View.VISIBLE

                binding.tvCompDataYear.text = binding.model?.workYear?.value
                binding.etCompDataAgency.setText(binding.model?.agency?.value)
                binding.etCompDataContact.setText(binding.model?.contract?.value)
                binding.etCompDataIntroduction.setText(binding.model?.introduction?.value)
                binding.etCompDataEducation.setText(binding.model?.education?.value)
                binding.etCompDataTraining.setText(binding.model?.training?.value)
                binding.etCompDataName.setText(binding.model?.name?.value)
                binding.etCompDataCredential.setText(binding.model?.credential?.value)
                binding.tvCompDataCity.setText(binding.model?.tvCity?.value)
                when (binding.model?.gender?.value) {
                    0 -> {
                        binding.btnDataRadio.check(R.id.radioButton2) //男
                    }
                    1 -> {
                        binding.btnDataRadio.check(R.id.radioButton3)  //女
                    }
                }

                val sb = StringBuilder()
                for (i in 0 until binding.model?.area?.value?.size!!) {
                    when (binding.model?.area?.value!![i]) {
                        1 -> sb.append("情绪管理")
                        2 -> sb.append("儿童心理")
                        3 -> sb.append("人格培养")
                        4 -> sb.append("专注力训练")
                        5 -> sb.append("网瘾厌学")
                        6 -> sb.append("亲子教育")
                        7 -> sb.append("人际交往")
                        8 -> sb.append("母婴家庭")
                        9 -> sb.append("婚恋情感")
                        10 -> sb.append("青少年成长")
                    }
                    sb.append(",")
                }
                binding.model?.tvArea?.value = sb.toString()

                binding.model?.isLoadFromSP?.value = false
            }
        })

        /** 加载服务器数据*/
        binding.model?.isLoadFromNetwork?.observe(viewLifecycleOwner, Observer { boolean ->
            if (boolean) {
                pDialog.dismiss()
                binding.btnCompDataSave.visibility = View.GONE

                binding.tvCompDataYear.setText(binding.model?.response?.value?.work_year.toString())
                binding.etCompDataContact.setText(binding.model?.response?.value?.contact)
                binding.etCompDataIntroduction.setText(binding.model?.response?.value?.introduction)
                binding.etCompDataEducation.setText(binding.model?.response?.value?.education)
                binding.etCompDataTraining.setText(binding.model?.response?.value?.training)
                binding.etCompDataName.setText(binding.model?.response?.value?.name)
                binding.etCompDataAgency.setText(binding.model?.response?.value?.agency)
                binding.etCompDataCredential.setText(binding.model?.response?.value?.credential)
                binding.tvCompDataCity.setText(binding.model?.response?.value?.city)
                when (binding.model?.response?.value?.gender) {
                    0 -> {
                        binding.btnDataRadio.check(R.id.radioButton2) //男
                    }
                    1 -> {
                        binding.btnDataRadio.check(R.id.radioButton3)  //女
                    }
                }

                val sb = StringBuilder()
                sb.append(binding.model?.response?.value?.area_one)
                sb.append(",")
                sb.append(binding.model?.response?.value?.area_two)
                sb.append(",")
                sb.append(binding.model?.response?.value?.area_three)
                binding.model?.tvArea?.value = sb.toString()

                binding.model?.isLoadFromNetwork?.value = false
            }
        })

        binding.model?.isEdit?.observe(viewLifecycleOwner, Observer { boolean ->

            Log.e("data", binding.model?.isFirst?.value!!.toString())
            if (binding.model?.isFirst?.value!!) {
                binding.model?.loadDataFromNetwork()
            } else {
                binding.model?.loadDataFromSP()
            }
            if (boolean) {
                /** 按下编辑*/
                binding.model?.title?.value = "提 交 资 料"
                binding.model?.rightTitle?.value = "取  消"

                binding.tvCompDataCity.isEnabled = true
                binding.etCompDataContact.isEnabled = true
                binding.etCompDataIntroduction.isEnabled = true
                binding.etCompDataAgency.isEnabled = true
                binding.etCompDataName.isEnabled = true
                binding.etCompDataCredential.isEnabled = false
                binding.tvCompDataYear.isEnabled = true
                binding.etCompDataEducation.isEnabled = true
                binding.etCompDataTraining.isEnabled = true

                for (i in 0 until binding.btnDataRadio.childCount) {
                    binding.btnDataRadio.getChildAt(i).isEnabled = true
                }
                binding.tvCompDataArea.isEnabled = true

                binding.etCompDataCertificatesLo.visibility = View.VISIBLE
                binding.etCompDataImageLo.visibility = View.VISIBLE
                binding.ckSecretLo.visibility = View.VISIBLE
                binding.btnCompDataSave.visibility = View.VISIBLE
                binding.btnCompDataSubmit.visibility = View.VISIBLE
            } else {
                /** 按下取消*/
                binding.model?.title?.value = "个 人 资 料"
                binding.model?.rightTitle?.value = "编  辑"

                binding.tvCompDataCity.isEnabled = false
                binding.etCompDataContact.isEnabled = false
                binding.etCompDataIntroduction.isEnabled = false
                binding.etCompDataAgency.isEnabled = false
                binding.etCompDataName.isEnabled = false
                binding.etCompDataCredential.isEnabled = false
                binding.etCompDataEducation.isEnabled = false
                binding.etCompDataTraining.isEnabled = false
                binding.tvCompDataYear.isEnabled = false
                for (i in 0 until binding.btnDataRadio.childCount) {
                    binding.btnDataRadio.getChildAt(i).isEnabled = false
                }
                binding.tvCompDataArea.isEnabled = false
                binding.etCompDataCertificatesLo.visibility = View.INVISIBLE
                binding.etCompDataImageLo.visibility = View.INVISIBLE
                binding.ckSecretLo.visibility = View.INVISIBLE
                binding.btnCompDataSave.visibility = View.INVISIBLE
                binding.btnCompDataSubmit.visibility = View.INVISIBLE
            }
        })

        /**
         * 选择从业年份
         */
        binding.tvCompDataYear.setOnClickListener {
            val cd = Calendar.getInstance()

            val dialog = DatePickerDialog(
                requireContext(),
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                listener,
                cd.get(Calendar.YEAR),
                cd.get(Calendar.MONTH),
                cd.get(Calendar.DATE)
            )
            dialog.show()

            val dp = findDatePicker(dialog.window?.decorView as ViewGroup)
            if (dp != null) {
                ((dp.getChildAt(0) as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(2).visibility =
                    View.GONE
                ((dp.getChildAt(0) as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(1).visibility =
                    View.GONE
            }
        }
        /**
         * 选择地区
         */

        binding.tvCompDataCity.setOnClickListener {
            val gridLayoutManager = GridLayoutManager(activity, 1)
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return 2;
                }
            }
            val builder = CircleDialog.Builder()
            Log.e("listssss", binding.model?.provinceList.toString())
            builder.setTitle("选取省份")
                .configItems { params -> params.dividerHeight = 0 }
                .setItems(binding.model?.provinceList!!, gridLayoutManager) { view, position ->
                    val list = binding.model?.cityList!![position].city
                    showCity(position, list,binding.model?.cityList!![position].province)
                    true
                }
                .setNegative("取消", null)
                .show(fragmentManager)


        }


        /** 返回按键*/
        binding.tvCompDataCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        /** 打开相册*/
        binding.btnCompDataImage.setOnClickListener {
            val chooseIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            try {
                startActivityForResult(chooseIntent, REQUEST_CODE_IMAGE)
            } catch (e: ActivityNotFoundException) {
                ToastUtils.showShort("没有找到相册")
            }
        }

        /** 打开文件夹*/
        binding.btnCompDataCertificates.setOnClickListener {
            /*
            /** 判断是否是小米手机，是就简单*/
            if (judgeMi()) {
                val chooseIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Files.getContentUri("external")
                )
                try {
                    startActivityForResult(chooseIntent, REQUEST_CODE_FILE)
                } catch (e: ActivityNotFoundException) {
                    ToastUtils.showShort("没有找到文件管理器")
                }

            /** 不是就根据类型打开管理器*/
            } else {

             */
            val intent = Intent(Intent.ACTION_GET_CONTENT)

            val rvListForV: MutableList<String> =
                ArrayList<String>()
            rvListForV.add("jpg/png格式")
            rvListForV.add("pdf/word格式")
            rvListForV.add("txt文档格式")

            CircleDialog.Builder() // .setTypeface(typeface)
                .setMaxHeight(0.7f)
                .configDialog { params: DialogParams ->
                    params.backgroundColorPress = Color.WHITE
                }
                .configItems { params: ItemsParams -> params.dividerHeight = 1 }
                .setItems(
                    rvListForV
                ) { view13: View?, position13: Int ->

                    when (position13) {
                        0 -> {
                            val chooseIntent = Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                            try {
                                startActivityForResult(chooseIntent, REQUEST_CODE_FILE_IMAGE)
                            } catch (e: ActivityNotFoundException) {
                                ToastUtils.showShort("没有找到相册")
                            }
                        }

                        1 -> {
                            intent.setType("application/msword")
                                .addCategory(Intent.CATEGORY_OPENABLE)
                            try {
                                startActivityForResult(
                                    Intent.createChooser(intent, "Choose File"),
                                    REQUEST_CODE_FILE
                                )
                            } catch (e: ActivityNotFoundException) {
                                ToastUtils.showShort("没有找到文件管理器")
                            }
                        }

                        2 -> {
                            intent.setType("text/plain").addCategory(Intent.CATEGORY_OPENABLE)
                            try {
                                startActivityForResult(
                                    Intent.createChooser(intent, "Choose File"),
                                    REQUEST_CODE_FILE
                                )
                            } catch (e: ActivityNotFoundException) {
                                ToastUtils.showShort("没有找到文件管理器")
                            }
                        }
                    }
                    true
                }
                .setNegative("取消", null)
                .show(fragmentManager)

        }

        /** navigationBar*/
        binding.model?.isShow?.observe(viewLifecycleOwner, Observer<Boolean> { boolean ->
            val navView: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
            navView.visibility = View.GONE
        })

        binding.model?.gender?.observe(viewLifecycleOwner, Observer<Int> { id ->
            binding.btnDataRadio.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.radioButton2 -> {
                        binding.model?.gender?.value = 0  //男
                    }
                    R.id.radioButton3 -> {
                        binding.model?.gender?.value = 1  //女
                    }
                }
            }
        })

        /** 提交*/
        binding.model?.isSubmit?.observe(viewLifecycleOwner, Observer { boolean ->
            if (boolean) {
                findNavController().popBackStack()
            }
        })

        /** 选取擅长领域*/
        binding.tvCompDataArea.setOnClickListener {
            binding.model?.area?.value?.clear()
            val array: Array<String?> = arrayOfNulls(10)
            for (area in binding.model?.areaList?.value!!) {
                array[area.id - 1] = area.name
            }
            if (binding.model?.areaList?.value!!.isEmpty()) {
                ToastUtils.showShort("暂时没有数据，请联系后台人员")
            } else {
                val checkAdapter = CheckAdapter(
                    requireContext(),
                    array
                )

                val builder = CircleDialog.Builder()
                builder.setTitle("擅长领域")
                builder.setSubTitle("最多可选3个")
                builder.setItems(
                    checkAdapter
                ) { parent, view12, position12, id ->
                    checkAdapter.toggle(position12, id.toInt())
                    false
                }
                builder.setPositive("确定") {
                    val sb = StringBuilder()
                    for (i in 0 until checkAdapter.saveChecked.size()) {
                        binding.model?.area?.value?.add(checkAdapter.saveChecked.keyAt(i) + 1)
                        sb.append(binding.model?.areaList?.value!![checkAdapter.saveChecked.keyAt(i)].name)
                        sb.append(",")
                    }
                    binding.model?.tvArea?.value = sb.toString()
                    ToastUtils.showShort("成功")
                }
                builder.setNegative("取消", View.OnClickListener {
                    ToastUtils.showShort("取消")
                    binding.model?.tvArea?.value = ""
                })
                builder.show(fragmentManager)
            }
        }

        /** 服务保密协议*/
        binding.model?.isCheckSeret?.observe(viewLifecycleOwner, Observer {
            val styleSpan = SpannableStringBuilder()
            styleSpan.append("心理咨询用户使用协议")
            binding.ckSecret.text = styleSpan
        })

        binding.ckSecret.setOnClickListener {
            binding.model?.isCheckSeret?.value = !binding.model?.isCheckSeret?.value!!
        }

        binding.ivSecretArray.setOnClickListener {
            binding.model?.onSave()
            findNavController().navigate(R.id.action_completeDataFragment_to_secretFragment)
        }
    }

    fun showCity(pos: Int,list: ArrayList<City>,provice:String) {
        val gridLayoutManager = GridLayoutManager(activity, 1)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return 2;
            }
        }
        val builder2 = CircleDialog.Builder()
        builder2.setTitle("选取城市")
            .configItems { params -> params.dividerHeight = 0 }
            .setItems(
                list,
                gridLayoutManager
            ) { view2, position2 ->
                dataViewModel.tvCity.value =
                     provice + list[position2].city
                dataViewModel.city.value =
                    list[position2].id.toString()
                true
            }
            .setNegative("取消", null)
            .show(fragmentManager)
    }

    private fun judgeMi(): Boolean {
        val manu = Build.MANUFACTURER
        return ("xiaomi".equals(manu, ignoreCase = true))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_IMAGE -> data?.let { handleImageRequestResult(data) }
                REQUEST_CODE_FILE -> data?.let { handleFileRequestResult(data) }
                REQUEST_CODE_FILE_IMAGE -> data?.let { handleFileImageRequestResult(data) }
                else -> Log.d("data", "Unknown request code.")
            }
        } else {
            Log.e("data", String.format("Unexpected Result code %s", resultCode))
        }
    }

    private fun handleFileImageRequestResult(intent: Intent) {
        val imageUri: Uri? = intent.clipData?.let {
            it.getItemAt(0).uri
        } ?: intent.data

        if (imageUri == null) {
            Log.e("data", "Invalid input image Uri.")
            return
        }
        val cursor = requireActivity().contentResolver.query(imageUri, null, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                binding.model?.filePath?.value = path
                try {
                    val imageFile = File(path)
                    binding.model?.fileName?.value = imageFile.name
                } catch (e: NullPointerException) {
                    ToastUtils.showShort("获取不到照片，请确认是否是照片")
                }
            }
            cursor.close()
        }
    }

    private fun handleFileRequestResult(intent: Intent) {
        val fileUri: Uri? = intent.clipData?.let {
            it.getItemAt(0).uri
        } ?: intent.data

        if (fileUri == null) {
            Log.e("data", "Invalid input image Uri.")
            return
        }

        Log.e("data", "${fileUri?.scheme} + ${fileUri?.path}")

        val columns = arrayOf(
            MediaStore.Files.FileColumns._ID
            , MediaStore.Files.FileColumns.MIME_TYPE
            , MediaStore.Files.FileColumns.SIZE
            , MediaStore.Files.FileColumns.DATE_MODIFIED
            , MediaStore.Files.FileColumns.DATA
        )
        /*
        val cursor= if(judgeMi()){
            requireActivity().contentResolver.query(fileUri, columns, null, null, null)
        }else {
            requireActivity().contentResolver.query(fileUri, null, null, null, null)
        }
         */
        val cursor =
            requireActivity().contentResolver.query(fileUri, columns, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val path =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
                binding.model?.filePath?.value = path
                try {
                    val file = File(path)
                    binding.model?.fileName?.value = file.name
                } catch (e: NullPointerException) {
                    ToastUtils.showShort("获取不到文件，请重试")
                }
            }
            cursor.close()
        }
    }

    private fun handleImageRequestResult(intent: Intent) {
        // If clipdata is available, we use it, otherwise we use data
        val imageUri: Uri? = intent.clipData?.let {
            it.getItemAt(0).uri
        } ?: intent.data

        if (imageUri == null) {
            Log.e("data", "Invalid input image Uri.")
            return
        }
        val cursor = requireActivity().contentResolver.query(imageUri, null, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                binding.model?.imagePath?.value = path
                try {
                    val imageFile = File(path)
                    binding.model?.imageName?.value = imageFile.name
                } catch (e: NullPointerException) {
                    ToastUtils.showShort("获取不到照片，请确认是否是照片")
                }
            }
            cursor.close()
        }
    }

    private fun findDatePicker(group: ViewGroup?): DatePicker? {
        if (group != null) {
            var i = 0
            val j = group.childCount
            while (i < j) {
                val child = group.getChildAt(i)
                if (child is DatePicker) {
                    return child
                } else if (child is ViewGroup) {
                    val result = findDatePicker(child)
                    if (result != null) return result
                }
                i++
            }
        }
        return null
    }
}


