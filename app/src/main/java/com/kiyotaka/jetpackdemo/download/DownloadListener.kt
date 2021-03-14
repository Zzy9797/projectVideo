package com.kiyotaka.jetpackdemo.download

interface DownloadListener {
    fun onProgress(process: Int)
    fun onSuccess()
    fun onFailed()
    fun onPaused()
    fun onCanceled()
}