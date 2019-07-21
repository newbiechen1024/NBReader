package com.example.newbiechen.nbreader.uilts

import android.util.Log
import androidx.annotation.Nullable
import com.example.newbiechen.nbreader.BuildConfig

object LogHelper {
    val TAG = "VidsMaker"
    var isLogEnabled = BuildConfig.DEBUG

    fun i(subTag: String, msg: String) {
        if (isLogEnabled) {
            Log.i(TAG, getLogMsg(subTag, msg))
        }
    }

    fun i(subTag: String, msg: String, tr: Throwable) {
        if (isLogEnabled) {
            Log.i(TAG, getLogMsg(subTag, msg), tr)
        }
    }

    fun w(subTag: String, msg: String) {
        if (isLogEnabled) {
            Log.w(TAG, getLogMsg(subTag, msg))
        }

    }

    fun w(subTag: String, msg: String, tr: Throwable) {
        if (isLogEnabled) {
            Log.w(TAG, getLogMsg(subTag, msg), tr)
        }

    }

    @Deprecated("") // 由于华为部分手机不能输出Debug级别log，不推荐使用
    fun d(subTag: String, msg: String) {
        if (isLogEnabled) {
            Log.d(TAG, getLogMsg(subTag, msg))
        }

    }

    @Deprecated("") // 由于华为部分手机不能输出Debug级别log，不推荐使用
    fun d(subTag: String, msg: String, tr: Throwable) {
        if (isLogEnabled) {
            Log.d(TAG, getLogMsg(subTag, msg), tr)
        }

    }

    fun e(subTag: String, msg: String) {
        if (isLogEnabled) {
            Log.e(TAG, getLogMsg(subTag, msg))
        }
    }

    fun e(subTag: String, msg: String, tr: Throwable) {
        if (isLogEnabled) {
            Log.e(TAG, getLogMsg(subTag, msg), tr)
        }
    }

    private fun getLogMsg(subTag: String, msg: String): String {
        val sb = StringBuffer()
            .append("{").append(Thread.currentThread().name).append("}")
            .append("[").append(subTag).append("] ")
            .append(msg)

        return sb.toString()
    }

    fun errorState(info: String) {
        if (isLogEnabled) {
            throw IllegalStateException(info, null)
        }
    }

    fun errorState(info: String, @Nullable cause: Throwable) {
        if (isLogEnabled) {
            throw IllegalStateException(info, cause)
        }
    }
}
