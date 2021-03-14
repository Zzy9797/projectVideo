package com.kiyotaka.jetpackdemo.http

import java.lang.Exception

class ResultException constructor(val code:Int,val msg:String): Exception() {
}