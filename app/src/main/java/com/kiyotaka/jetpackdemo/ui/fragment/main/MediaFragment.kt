package com.kiyotaka.jetpackdemo.ui.fragment.main

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.kiyotaka.jetpackdemo.R
import com.kiyotaka.jetpackdemo.databinding.FragmentMediaBinding
import com.kiyotaka.jetpackdemo.model.CustomViewModelProvider
import com.kiyotaka.jetpackdemo.model.MediaViewModel
import kotlinx.android.synthetic.main.fragment_media.*
import java.io.File

class MediaFragment : Fragment() {

    private val REQUEST_MP3_FILE = 100
    private val mediaViewModel: MediaViewModel by viewModels {
        CustomViewModelProvider.providerMediaModel()
    }
    private lateinit var binding: FragmentMediaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_media,
            container,
            false
        )
        initData()
        onSubscribeUI()
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun onSubscribeUI() {
        binding.model?.isShow?.observe(viewLifecycleOwner, Observer<Boolean> { boolean ->
            val navView: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
            navView.visibility = View.GONE
        })

        binding.tvMediaCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvMediaName.setOnClickListener {
            openMedia()
        }
    }

    private fun initData() {
        binding.lifecycleOwner = this
        binding.model = mediaViewModel

        binding.model?.queryMP3()
    }

    private fun openMedia() {
        try {
            val chooseIntent = Intent(Intent.ACTION_GET_CONTENT)
            chooseIntent.type = "audio/mpeg"
            startActivityForResult(
                Intent.createChooser(chooseIntent, "choose music"),
                REQUEST_MP3_FILE
            )
        } catch (e: ActivityNotFoundException) {
            ToastUtils.showShort("找不到文件管理器，请检查一下手机吧！")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_MP3_FILE -> {
                    data?.let { handleMP3RequestResult(data) }
                }
            }
        }
    }

    private fun handleMP3RequestResult(intent: Intent) {
        val musicUri: Uri? = intent.clipData?.let {
            it.getItemAt(0).uri
        } ?: intent.data

        if (musicUri == null) {
            Log.e("music", "Invalid input music Uri.")
            return
        }

        val columns = arrayOf(
            MediaStore.Audio.AudioColumns._ID
            , MediaStore.Audio.AudioColumns.MIME_TYPE
            , MediaStore.Audio.AudioColumns.SIZE
            , MediaStore.Audio.AudioColumns.DATE_MODIFIED
            , MediaStore.Audio.AudioColumns.DATA
        )

        val cursor = requireActivity().contentResolver.query(musicUri, columns, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                binding.model?.musicPath?.value = path
                try {
                    val file = File(path)
                    binding.model?.MP3Name?.value = file.name
                } catch (e: NullPointerException) {
                    ToastUtils.showShort("获取不到音乐，请确认是否是音乐")
                }
            }
            cursor.close()
        }
    }
}
