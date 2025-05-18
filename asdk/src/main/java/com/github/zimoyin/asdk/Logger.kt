package com.github.zimoyin.asdk

import android.util.Log

const val TAG = "AutoSdk"


object logger {
    fun info(msg: String) {
        Log.i(TAG, msg)
    }

    fun warn(msg: String) {
        Log.w(TAG, msg)
    }

    fun error(msg: String) {
        Log.e(TAG, msg)
    }

    fun error(e: Throwable) {
        Log.e(TAG, e.message, e)
    }

    fun debug(msg: String) {
        Log.d(TAG, msg)
    }

    fun i(msg: String) {
        info(msg)
    }

    fun w(msg: String) {
        warn(msg)
    }

    fun e(msg: String) {
        error(msg)
    }

    fun d(msg: String) {
        debug(msg)
    }

    fun i(msg: String, vararg args: Any?) {
        info(msg.format(*args))
    }


}