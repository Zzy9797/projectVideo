package com.kiyotaka.jetpackdemo.http

import android.util.Log
import com.google.gson.Gson
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException
import java.lang.Error
import java.lang.reflect.Type

class GsonResponseBodyConverter<T> constructor(
    val gson: Gson
    ,val type: Type
) : Converter<ResponseBody, T> {


    override fun convert(value: ResponseBody): T? {
        val response = value.string()
        val httpResult =gson.fromJson(response,Response::class.java)
        if(httpResult.code == 200 || httpResult.code == 201){
            Log.e("http",httpResult.code.toString())
            return gson.fromJson(response,type)
        }else{
            Log.e("http",httpResult.body.toString())
            val errorResponse = gson.fromJson(response,ErrorResponse::class.java)
            throw ResultException(errorResponse.code,errorResponse.msg)
        }
    }
}