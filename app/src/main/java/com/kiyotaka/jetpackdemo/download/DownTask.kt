package com.kiyotaka.jetpackdemo.download

import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile

class DownTask : AsyncTask<String, Int, Int> {

    val TYPE_SUCCESS: Int = 1
    val TYPE_FAILED: Int = 2
    val TYPE_PAUSED: Int = 3
    val TYPE_CANCELED: Int = 4

    private var isCanceled = false
    private var isPaused = false
    private var lastProgress = 0
    var filepath = ""
    private var downloadListener: DownloadListener

    constructor(listener: DownloadListener) {
        downloadListener = listener
    }

    override fun doInBackground(vararg p0: String): Int {
        var bs: InputStream? = null
        var savedFile: RandomAccessFile? = null
        var file: File? = null
        try {
            var dowmloadedLength: Long = 0
            val downloadUrl: String = p0.get(0)
            val fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"))
            val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
            filepath = directory + fileName
            Log.e("url", directory + fileName)
            file = File(directory + fileName)
            if (file.exists()) {
                dowmloadedLength = file.length()
            }
            val contentLength = getContentLength(downloadUrl)
            if (contentLength == 0L) {
                return TYPE_FAILED
            } else if (contentLength == dowmloadedLength) {
                return TYPE_SUCCESS
            }
            val client = OkHttpClient()
            val request = Request.Builder()
                .addHeader("RANGE", "bytes=$dowmloadedLength-")
                .url(downloadUrl)
                .build()
            val response = client.newCall(request).execute()
            if (response != null) {
                bs = response.body!!.byteStream()
                savedFile = RandomAccessFile(file, "rw")
                savedFile.seek(dowmloadedLength)
                val b = ByteArray(1024)
                var total = 0
                var len: Int
                while (bs.read(b).also { len = it } != -1) {
                    total += if (isCanceled) {
                        return TYPE_CANCELED
                    } else if (isPaused) {
                        return TYPE_PAUSED
                    } else {
                        len
                    }
                    savedFile.write(b, 0, len)
                    val progress = ((total + dowmloadedLength) * 100 / contentLength).toInt()
                    publishProgress(progress)
                }
                response.body!!.close()
                return TYPE_SUCCESS
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                bs?.close()
                savedFile?.close()
                if (isCanceled && file != null) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return TYPE_FAILED
    }


    override fun onProgressUpdate(vararg values: Int?) {
        val progress = values[0]
        if(progress!=null) {
            if (progress > lastProgress) {
                downloadListener.onProgress(progress)
                lastProgress = progress
            }
        }
    }


    override fun onPostExecute(status: Int?) {
        Log.e("download",status.toString())
        when (status) {
            TYPE_SUCCESS -> downloadListener.onSuccess()
            TYPE_FAILED -> downloadListener.onFailed()
            TYPE_PAUSED -> downloadListener.onPaused()
            TYPE_CANCELED -> downloadListener.onCanceled()
            else -> {
            }
        }
    }

    fun pauseDownload() {
        isPaused = true
    }

    fun cancelDownload() {
        isCanceled = true
    }

    @Throws(IOException::class)
    private fun getContentLength(downloadUrl: String): Long {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(downloadUrl)
            .build()
        val response = client.newCall(request).execute()
        if (response != null && response.isSuccessful) {
            val contentLength = response.body!!.contentLength()
            response.body!!.close()
            return contentLength
        }
        return 0
    }
}