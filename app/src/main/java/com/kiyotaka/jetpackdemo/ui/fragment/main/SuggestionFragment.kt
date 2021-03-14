package com.kiyotaka.jetpackdemo.ui.fragment.main

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.kiyotaka.jetpackdemo.R
import com.kiyotaka.jetpackdemo.common.BaseApplication
import com.kiyotaka.jetpackdemo.databinding.SuggestionFragmentBinding
import com.kiyotaka.jetpackdemo.http.APIs
import com.kiyotaka.jetpackdemo.http.AppContent
import com.kiyotaka.jetpackdemo.http.BaseBean
import com.kiyotaka.jetpackdemo.http.RetrofitClient
import com.kiyotaka.jetpackdemo.model.CustomViewModelProvider
import com.kiyotaka.jetpackdemo.model.SuggestionViewModel
import com.kiyotaka.jetpackdemo.ui.activity.MainActivity
import com.mylhyl.circledialog.CircleDialog
import com.mylhyl.circledialog.params.DialogParams
import com.mylhyl.circledialog.params.ItemsParams
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_app.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SuggestionFragment : Fragment() {

    private val model: SuggestionViewModel by viewModels {
        CustomViewModelProvider.providerSuggestionModel()
    }
    private val pDialog: SweetAlertDialog by lazy {
        SweetAlertDialog(requireActivity(), SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("上传中...")
    }

    private lateinit var binding: SuggestionFragmentBinding
    private lateinit var imageUri: Uri
    private var imageUrl: String? = null
    private var cameraImageUrl: String = ""
    private val TAKE_PHOTO = 1
    private val CHOOSE_PHOTO = 2
    private val imageList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.suggestion_fragment,
            container,
            false
        )
        initData()
        initView()
        onSubscribeUi()
        return binding.root
    }

    private fun onSubscribeUi() {
        binding.tvSuggestionCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.model?.isShow?.observe(viewLifecycleOwner, Observer { boolean ->
            val navView: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
            navView.visibility = View.GONE
        })

        binding.suggestionSubmit.setOnClickListener {
            val issue = binding.suggestionIssue.text.toString()
            val userphone = binding.suggestionConnect.text.toString()
            if (issue.isEmpty() || userphone.isEmpty()) {
                ToastUtils.showShort("请填好反馈内容以及您的联系方式！")
            } else {
                uploadSuggestion(issue, userphone)
            }
        }
        binding.suggestionSelect.setOnClickListener{
            if (imageList.size < 5) {
                showSelectDialog()
            } else {
                ToastUtils.showShort("最多添加5张图片，长按删除照片。")
            }
        }
    }

    private fun initData() {
        binding.lifecycleOwner = this
        binding.model = model
    }

    private fun initView() {
        binding.suggestionView1.setOnLongClickListener(View.OnLongClickListener {
            CircleDialog.Builder().setTitle("是否删除当前照片？").setPositive("是") {
                if (imageList.size >= 1) {
                    imageList.removeAt(0)
                    showImageList()
                }
            }.setNegative("否") {
            }.show(fragmentManager)
            false
        })
        binding.suggestionView2.setOnLongClickListener(View.OnLongClickListener {
            CircleDialog.Builder().setTitle("是否删除当前照片？").setPositive("是") {
                if (imageList.size >= 2) {
                    imageList.removeAt(1)
                    showImageList()
                }
            }.setNegative("否") {
            }.show(fragmentManager)
            false
        })
        binding.suggestionView3.setOnLongClickListener(View.OnLongClickListener {
            CircleDialog.Builder().setTitle("是否删除当前照片？").setPositive("是") {
                if (imageList.size >= 3) {
                    imageList.removeAt(2)
                    showImageList()
                }
            }.setNegative("否") {
            }.show(fragmentManager)
            false
        })
        binding.suggestionView4.setOnLongClickListener(View.OnLongClickListener {
            CircleDialog.Builder().setTitle("是否删除当前照片？").setPositive("是") {
                if (imageList.size >= 4) {
                    imageList.removeAt(3)
                    showImageList()
                }
            }.setNegative("否") {
            }.show(fragmentManager)
            false
        })
        binding.suggestionView5.setOnLongClickListener(View.OnLongClickListener {
            CircleDialog.Builder().setTitle("是否删除当前照片？").setPositive("是") {
                if (imageList.size >= 5) {
                    imageList.removeAt(4)
                    showImageList()
                }
            }.setNegative("否") {
            }.show(fragmentManager)
            false
        })
    }


    private fun showSelectDialog() {
        val items = arrayOf("从拍摄选取", "从相册选取")
        CircleDialog.Builder() // .setTypeface(typeface)
            .setMaxHeight(0.7f)
            .configDialog { params: DialogParams ->
                params.backgroundColorPress = Color.WHITE
            }
            .configItems { params: ItemsParams -> params.dividerHeight = 1 }
            .setItems(
                items
            ) { view13: View?, position13: Int ->

                when (position13) {
                    0 -> {
                        if (ContextCompat.checkSelfPermission(
                                activity as AppCompatActivity,
                                Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                activity as AppCompatActivity,
                                Array(1) { Manifest.permission.CAMERA },
                                2
                            )
                        } else {
                            openCamera()
                        }
                    }
                    1 -> {
                        if (ContextCompat.checkSelfPermission(
                                activity as AppCompatActivity,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                activity as AppCompatActivity,
                                Array(1) { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                1
                            )
                        } else {
                            openAlbum()
                        }
                    }
                }
                true
            }
            .setNegative("取消", null)
            .show(fragmentManager)
    }

    /**
     * 新建文件夹 suggestion存放拍摄的图片
     */
    private fun openCamera() {
        val file = File(getSDPath() + "/consultVideo/suggestion/")
        if (!file.exists()) file.mkdirs()

        cameraImageUrl = getSDPath() + "/consultVideo/suggestion/" + getPhotoFileName()

        val outputImage = File(cameraImageUrl)
        try {
            if (!outputImage.exists()) {
                outputImage.createNewFile()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri =
                FileProvider.getUriForFile(
                    activity as AppCompatActivity,
                    "${BaseApplication.context.packageName}.provider",
                    outputImage
                )
        } else {
            imageUri = Uri.fromFile(outputImage)
        }
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, TAKE_PHOTO)
    }

    private fun getSDPath(): String? {
        var sdDir: File? = null
        val sdCardExsit = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
        if (sdCardExsit) {
            sdDir = Environment.getExternalStorageDirectory()
        }
        return sdDir.toString()
    }

    private fun getPhotoFileName(): String? {
        val date = Date(System.currentTimeMillis())
        val dateFormat = SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss")
        return dateFormat.format(date).toString() + ".jpg"
    }

    private fun openAlbum() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, CHOOSE_PHOTO)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum()
                } else {
                    ToastUtils.showShort("没有权限")
                }
            }
            2 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    ToastUtils.showShort("没有权限")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            TAKE_PHOTO -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (cameraImageUrl != "") {
                        imageList.add(cameraImageUrl)
                    }
                    showImageList()
                } else {
                    val outputImage = File(cameraImageUrl)
                    try {
                        if (outputImage.exists()) {
                            outputImage.delete()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            CHOOSE_PHOTO -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        if (Build.VERSION.SDK_INT >= 19) {
                            handleImageOnKitKat(data)
                        } else {
                            handleImageBeforeKitKat(data)
                        }
                    }
                }
            }
        }
    }


    private fun handleImageBeforeKitKat(data: Intent) {
        val uri = data.data
        if (uri != null) {
            val imagePath = getImagePath(uri, null)
            if (imagePath != null)
                imageList.add(imagePath)
            showImageList()
        }
    }

    @TargetApi(19)
    private fun handleImageOnKitKat(data: Intent) {
        var imagePath: String? = null
        val uri = data.data
        if (DocumentsContract.isDocumentUri(activity, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents".equals(uri!!.authority)) {
                val id = docId.split(":")[1]
                val selection = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents".equals(uri.authority)) {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    docId.toLong()
                )
                imagePath = getImagePath(contentUri, null)
            }
        } else if ("content".equals(uri!!.scheme)) {
            imagePath = getImagePath(uri, null)
        } else if ("file".equals(uri.scheme)) {
            imagePath = uri.path
        }
        imageUrl = imagePath
        if (imagePath != null)
            imageList.add(imagePath)
        showImageList()
    }

    /**
     * 获取图片path
     */
    private fun getImagePath(uri: Uri, selection: String?): String? {
        var path: String? = null
        val cursor = requireActivity().contentResolver.query(uri, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }

    /**
     * 遍历bitmap展示图片并且回收资源，避免太大了溢出
     */
    private fun showImageList() {
        binding.suggestionView1.setImageBitmap(null)
        binding.suggestionView2.setImageBitmap(null)
        binding.suggestionView3.setImageBitmap(null)
        binding.suggestionView4.setImageBitmap(null)
        binding.suggestionView5.setImageBitmap(null)
        var count = 1
        imageList.forEach {
            try {
                val options = BitmapFactory.Options()
                options.inSampleSize = 4
                val bitmap = BitmapFactory.decodeFile(it, options)

                val matrix = Matrix()  //出来的图片都会被旋转了，我也不知道为什么，所以转回来。。。
               // matrix.postRotate(360f)
                val rotatedBitMap =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                when (count) {
                    1 -> binding.suggestionView1.setImageBitmap(rotatedBitMap)
                    2 -> binding.suggestionView2.setImageBitmap(rotatedBitMap)
                    3 -> binding.suggestionView3.setImageBitmap(rotatedBitMap)
                    4 -> binding.suggestionView4.setImageBitmap(rotatedBitMap)
                    5 -> binding.suggestionView5.setImageBitmap(rotatedBitMap)
                    else -> ToastUtils.showShort("查找照片失败")
                }
                count++
                /*
                if (!bitmap.isRecycled()) {
                    bitmap.recycle() //回收图片所占的内存
                    System.gc()//提醒系统及时回收
                }

                 */
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }

    }

    /**
     * 压缩一下图片，避免原图太大了
     */
    private fun compressImage(list: MutableList<String>): MutableList<File> {
        val resultList: MutableList<File> = mutableListOf()

        list.forEach {
            val oldFile = File(it)
            val quality = 80
            val bitmap = getSmallBitmap(it)
            val targetPath = getSDPath() + "/consultVideo/cache/" + oldFile.name
            val outputFile = File(targetPath)
            try {
                if (!outputFile.exists()) {
                    outputFile.parentFile.mkdirs()
                } else {
                    outputFile.delete()
                }
                val out = FileOutputStream(outputFile)
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, quality, out)
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
                resultList.add(oldFile)
            }
            resultList.add(outputFile)
        }

        return resultList
    }

    private fun getSmallBitmap(filePath: String?): Bitmap? {
        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inJustDecodeBounds = true //只解析图片边沿，获取宽高
        BitmapFactory.decodeFile(filePath, options)
        // 计算缩放比
        options.inSampleSize = calculateInSampleSize(options, 480, 800)
        // 完整解析图片返回bitmap
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filePath, options)
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int, reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }

    /**
     * 上传图片
     */
    private fun uploadSuggestion(issue: String, mobile_phone: String) {
        pDialog.show()

        val client = Retrofit.Builder()
            .baseUrl("https://api.gdbrainview.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val imageFiles = compressImage(imageList)
        val requestBodyBuilder = MultipartBody.Builder()
        try {
            requestBodyBuilder.setType(MultipartBody.FORM)
            if(imageFiles.isNotEmpty()) {
                for (i in 0 until imageFiles.size) {
                    val imageBody: RequestBody =
                        RequestBody.create("image/jpg".toMediaTypeOrNull(), imageFiles[i])
                    requestBodyBuilder.addFormDataPart(
                        "image${i + 1}",
                        imageFiles[i].name,
                        imageBody
                    )
                }
            }
            requestBodyBuilder.addFormDataPart("phone", MainActivity.phone)
            requestBodyBuilder.addFormDataPart("issue", issue)
            requestBodyBuilder.addFormDataPart("mobile_phone", mobile_phone)

            val requestBody = requestBodyBuilder.build()
            val observe: Observable<BaseBean<String>> =
                client.create(APIs::class.java).postSuggestion(requestBody)
            observe.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object :
                    io.reactivex.Observer<BaseBean<String>> {
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: BaseBean<String>) {
                        ToastUtils.showShort("提交成功")
                        binding.suggestionIssue.setText("")
                        binding.suggestionConnect.setText("")
                        binding.suggestionView1.setImageBitmap(null)
                        binding.suggestionView2.setImageBitmap(null)
                        binding.suggestionView3.setImageBitmap(null)
                        binding.suggestionView4.setImageBitmap(null)
                        binding.suggestionView5.setImageBitmap(null)
                        imageList.clear()
                        val file: Array<File>? =
                            File(getSDPath() + "/consultVideo/cache/").listFiles()
                        file?.forEach { it.delete() }
                        pDialog.dismiss()
                    }

                    override fun onError(e: Throwable) {
                        ToastUtils.showShort("${e.message}")
                        Log.e("sugg",e.message)
                        pDialog.dismiss()
                        val file: Array<File>? =
                            File(getSDPath() + "/consultVideo/cache/").listFiles()
                        file?.forEach { it.delete() }
                    }

                })
        }catch (e: Throwable) {
            ToastUtils.showShort("获取文件失败！")
        }
    }
}