package com.newbiechen.nbreader.uilts

import android.content.Context
import android.os.Environment

/**
 *  author : newbiechen
 *  date : 2019-09-18 17:30
 *  description :
 */

object FileUtil {

    // 获取 Cache 文件夹
    fun getCachePath(context: Context): String {
        return if (isSdCardExist()) {
            context.externalCacheDir.absolutePath
        } else {
            context.cacheDir.absolutePath
        }
    }

    //判断是否挂载了SD卡
    fun isSdCardExist(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }
}