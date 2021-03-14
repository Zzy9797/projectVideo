package com.kiyotaka.jetpackdemo.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.kiyotaka.jetpackdemo.common.BaseApplication
import com.kiyotaka.jetpackdemo.db.repository.UserRepository
import com.kiyotaka.jetpackdemo.http.*
import com.kiyotaka.jetpackdemo.ui.activity.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class DataViewModel constructor(
    private val repository: UserRepository
) : ViewModel() {
    val isShow = MutableLiveData(false)

    val name = MutableLiveData("") //姓名
    val gender = MutableLiveData(0) //性别
    val agency = MutableLiveData("") //机构
    val workYear = MutableLiveData("")  //从业时间
    val contract = MutableLiveData("")   //联系方式
    val area = MutableLiveData<ArrayList<Int>>()   //擅长领域
    val areaList = MutableLiveData<ArrayList<Area>>()
    val city = MutableLiveData("")  //城市
    var cityList = ArrayList<CityData>()
    val provinceList = ArrayList<String>()
    val introduction = MutableLiveData("") // 个人介绍
    val education = MutableLiveData("") // 教育经历
    val training = MutableLiveData("") // 培训经历
    val imagePath = MutableLiveData("")
    val filePath = MutableLiveData("")
    val imageName = MutableLiveData("")
    val fileName = MutableLiveData("")
    val credential = MutableLiveData("")

    val enable = MutableLiveData(false)
    val isSubmit = MutableLiveData(false)
    val isLoadFromNetwork = MutableLiveData(false)
    val isLoadFromSP = MutableLiveData(false)
    val tvArea = MutableLiveData("")
    val tvCity = MutableLiveData("")
    val isEdit = MutableLiveData(false)
    val title = MutableLiveData("个 人 资 料")
    val rightTitle = MutableLiveData("编  辑")
    val response = MutableLiveData<BashResponse>()
    val isFirst = MutableLiveData(false)
    val isCheckSeret = MutableLiveData(false)

    /**
     * 初始化
     */

    fun initData(boolean: Boolean) {
        checkUpFirst(boolean)
    }

    /**
     * 检查是否是第一次提交
     */
    fun checkUpFirst(boolean: Boolean) {
        val checkUpFirst = CheckUpFirst(MainActivity.phone.toLong())
        val gson = Gson()
        val json = gson.toJson(checkUpFirst)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    RetrofitClient.reqApi.postCheckUp(body)
                }
                if (data.code == 200) {
                    isFirst.value = data.data
                    if (!isFirst.value!!) {
                        ToastUtils.showShort("您还没有提交过资料，请点击右上角编辑按钮开始编辑资料并提交吧！")
                        loadDataFromSP()
                        isCheckSeret.value = false
                    } else {
                        if(boolean) {
                            loadDataFromNetwork()
                        }else{
                            loadDataFromSP()
                        }
                        isCheckSeret.value = true
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                if (e is ResultException) {
                    ToastUtils.showShort(e.msg)
                } else {
                    ToastUtils.showShort("服务器异常，请稍后再试")
                }
            }
        }

    }

    /**
     * 保存
     */
    fun onSave() {
        val sp = BaseApplication.context.getSharedPreferences(
            "data${MainActivity.phone}",
            Context.MODE_PRIVATE
        )
        val ed = sp.edit()
        ed.putString("name", name.value)
        ed.putInt("gender", gender.value!!)
        ed.putString("workyear", workYear.value)
        ed.putString("agency", agency.value)
        ed.putString("contact", contract.value)
        ed.putString("introduction", introduction.value)
        ed.putString("education",education.value)
        ed.putString("training",training.value)
        ed.putString("credential", credential.value)
        ed.putString("tvCity",tvCity.value)
        ed.putString("city",city.value)

        when (area.value!!.size) {
            1 -> {
                ed.putInt("area_one", area.value!![0])
                ed.putInt("area_two", -1)
                ed.putInt("area_three", -1)
            }

            2 -> {
                ed.putInt("area_one", area.value!![0])
                ed.putInt("area_two", area.value!![1])
                ed.putInt("area_three", -1)
            }

            3 -> {
                ed.putInt("area_one", area.value!![0])
                ed.putInt("area_two", area.value!![1])
                ed.putInt("area_three", area.value!![2])
            }
        }

        ed.apply()
        ToastUtils.showShort("保存成功")
    }

    /**
     * 从SP加载数据
     */
    fun loadDataFromSP() {
        Log.e("data","sp")
        val sp = BaseApplication.context.getSharedPreferences(
            "data${MainActivity.phone}",
            Context.MODE_PRIVATE
        )
        name.value = sp.getString("name", "")
        gender.value = sp.getInt("gender", 0)
        agency.value = sp.getString("agency", "")
        workYear.value = sp.getString("workyear", "")
        contract.value = sp.getString("contact", "")
        introduction.value = sp.getString("introduction", "")
        credential.value = sp.getString("credential", "")
        tvCity.value = sp.getString("tvCity","")
        city.value = sp.getString("city","1")

        val areaOne = sp.getInt("area_one", -1)
        val areaTwo = sp.getInt("area_two", -1)
        val areaThree = sp.getInt("area_three", -1)

        area.value = arrayListOf()

        if (areaOne != -1) {
            area.value!!.add(areaOne)
        }

        if (areaTwo != -1) {
            area.value!!.add(areaTwo)
        }

        if (areaThree != -1) {
            area.value!!.add(areaThree)
        }
        isLoadFromSP.value = true
    }

    /**
     * 从服务器加载数据
     */
    fun loadDataFromNetwork() {
        Log.e("load", "fromnet")
        val postBash = PostBash(MainActivity.phone.toLong())
        val gson = Gson()
        val json = gson.toJson(postBash)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    RetrofitClient.reqApi.queryBash(body)
                }
                if (data.code == 200) {
                    response.value = data.data[0]
                    area.value = arrayListOf()

                    if (response.value!!.area_one != "") {
                        area.value!!.add(transferArea(response.value!!.area_one))
                    }

                    if (response.value!!.area_two != "") {
                        area.value!!.add(transferArea(response.value!!.area_two))
                    }

                    if (response.value!!.area_three != "") {
                        area.value!!.add(transferArea(response.value!!.area_three))
                    }
                    workYear.value = response.value!!.work_year.toString()
                    tvCity.value = response.value!!.city
                    city.value = "0"    ////////////////这里暂时0，等后台传个city过来
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                if (e is ResultException) {
                    ToastUtils.showShort(e.msg)
                } else {
                    ToastUtils.showShort("服务器异常，请稍后再试")
                }
            } finally {
                isLoadFromNetwork.value = true
            }
        }
    }


    /**
     *名字
     */
    fun onNameChanged(s: CharSequence) {
        name.value = s.toString()
        judgeEnable()
    }

    /**
     * 机构
     */
    fun onAgencyChanged(s: CharSequence) {
        agency.value = s.toString()
        judgeEnable()
    }

    /**
     * 从业时间
     */
    fun onWorkYearChanged(s: CharSequence) {
        workYear.value = s.toString()
        judgeEnable()
    }

    /**
     * 个人资质
     */
    fun onCredentialChanged(s: CharSequence) {
        credential.value = s.toString()
        judgeEnable()
    }

    /**
     * 联系方式
     */
    fun onContractChanged(s: CharSequence) {
        contract.value = s.toString()
        judgeEnable()
    }

    /**
     * 简介
     */
    fun onIntroductionChanged(s: CharSequence) {
        introduction.value = s.toString()
        judgeEnable()
    }

    /**
     * 教育经历
     */
    fun onEducationChanged(s: CharSequence) {
        education.value = s.toString()
        judgeEnable()
    }

    /**
     * 培训经历
     */
    fun onTraininfChanged(s: CharSequence) {
        training.value = s.toString()
        judgeEnable()
    }

    /**
     * 编辑
     */
    fun edit() {
        isEdit.value = !isEdit.value!!
    }

    /**
     * 提交
     */
    fun onSubmit() {
        if (area.value.isNullOrEmpty()) {
            ToastUtils.showShort("请选择擅长领域!")
            return
        }
        if(city.value.isNullOrBlank()){
            ToastUtils.showShort("请选择地区！")
            return
        }
        if (!isCheckSeret.value!!) {
            ToastUtils.showShort("请勾选同意服务保密协议！")
            return
        }
        judgeEnable()
        if (enable.value!!) {
            val requestBodyBuilder = MultipartBody.Builder()

            try {

                val imageFile = File(imagePath.value!!)
                val imageBody: RequestBody =
                    RequestBody.create("image/jpg".toMediaTypeOrNull(), imageFile)
                requestBodyBuilder.setType(MultipartBody.FORM)
                requestBodyBuilder.addFormDataPart("head_portrait", imageFile.name, imageBody)

                val certificatesFile = File(filePath.value!!)
                val fileBody: RequestBody =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), certificatesFile)
                requestBodyBuilder.setType(MultipartBody.FORM)
                requestBodyBuilder.addFormDataPart("certificates", certificatesFile.name, fileBody)

                requestBodyBuilder.addFormDataPart("phone", MainActivity.phone)
                requestBodyBuilder.addFormDataPart("name", name.value!!)
                requestBodyBuilder.addFormDataPart("work_year", workYear.value!!)
                requestBodyBuilder.addFormDataPart("gender", gender.value!!.toString())
                requestBodyBuilder.addFormDataPart("contact", contract.value!!)
                requestBodyBuilder.addFormDataPart("agency", agency.value!!)
                requestBodyBuilder.addFormDataPart("introduction", introduction.value!!)
                requestBodyBuilder.addFormDataPart("education", education.value!!)
                requestBodyBuilder.addFormDataPart("training", training.value!!)

                requestBodyBuilder.addFormDataPart("city",city.value!!)
                Log.e("city",city.value)

                when (area.value!!.size) {
                    1 -> {
                        requestBodyBuilder.addFormDataPart("area_one", area.value!![0].toString())
                        requestBodyBuilder.addFormDataPart("area_two", "")
                        requestBodyBuilder.addFormDataPart("area_three", "")
                    }

                    2 -> {
                        requestBodyBuilder.addFormDataPart("area_one", area.value!![0].toString())
                        requestBodyBuilder.addFormDataPart("area_two", area.value!![1].toString())
                        requestBodyBuilder.addFormDataPart("area_three", "")
                    }

                    3 -> {
                        requestBodyBuilder.addFormDataPart("area_one", area.value!![0].toString())
                        requestBodyBuilder.addFormDataPart("area_two", area.value!![1].toString())
                        requestBodyBuilder.addFormDataPart("area_three", area.value!![2].toString())
                    }
                }

                val requestBody = requestBodyBuilder.build()


                viewModelScope.launch {
                    try {
                        val data = withContext(Dispatchers.IO) {
                            RetrofitClient.reqApi.postBash(requestBody)
                        }
                        ToastUtils.showShort(data.msg)
                        if (data.code == 201 || data.code == 200) {
                            isSubmit.value = true
                            val sp = BaseApplication.context.getSharedPreferences(
                                "data${MainActivity.phone}",
                                Context.MODE_PRIVATE
                            )
                            val ed = sp.edit()
                            ed.clear()
                            ed.apply()
                        }
                    } catch (e: Throwable) {
                        Log.e("http", e.message)
                        e.printStackTrace()
                        if (e is ResultException) {
                            ToastUtils.showShort(e.msg)
                        } else {
                            ToastUtils.showShort("服务器异常，请稍后再试")
                        }
                    }

                }
            } catch (e: Throwable) {
                ToastUtils.showShort("获取文件失败！")
            }
        } else {
            ToastUtils.showShort("请填好所有项")
        }
    }

    /**
     * 获取领域
     */
    fun getArea() {
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    RetrofitClient.reqApi.getAreaAll()
                }
                areaList.value = data.data
            } catch (e: Throwable) {
                e.printStackTrace()
                if (e is ResultException) {
                    ToastUtils.showShort(e.msg)
                } else {
                    ToastUtils.showShort("获取擅长领域失败")
                }
            }
        }
    }

    /**
     * 获取地区信息
     */
    fun getCity() {
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    RetrofitClient.reqApi.getCity()
                }
                if(data.code == 200) {
                    provinceList.clear()
                    cityList.clear()
                    cityList = data.data
                    for(i:Int in 0 until cityList.size){
                        provinceList.add(cityList[i].province)
                    }
                }
            } catch (e: Throwable) {
                Log.e("city",e.message)
                e.printStackTrace()
                if (e is ResultException) {
                    ToastUtils.showShort(e.msg)
                } else {
                    ToastUtils.showShort("获取城市失败")
                }
            }
        }
    }

    fun judgeEnable() {
        isSubmit.value = false
        enable.value = name.value!!.isNotEmpty()
                && agency.value!!.isNotEmpty()
                && contract.value!!.isNotEmpty()
                && workYear.value!!.isNotEmpty()
                && introduction.value!!.isNotEmpty()
                && imagePath.value!!.isNotEmpty()
                && filePath.value!!.isNotEmpty()
                && education.value!!.isNotEmpty()
                && training.value!!.isNotEmpty()
    }

    private fun transferArea(s: String) =
        when (s) {
            "情绪管理" -> 1
            "儿童心理" -> 2
            "人格培养" -> 3
            "专注力训练" -> 4
            "网瘾厌学" -> 5
            "亲子教育" -> 6
            "人际交往" -> 7
            "母婴家庭" -> 8
            "婚恋情感" -> 9
            "青少年成长" -> 10
            else -> -1
        }

}

