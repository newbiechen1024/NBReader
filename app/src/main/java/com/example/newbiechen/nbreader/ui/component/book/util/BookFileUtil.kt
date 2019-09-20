package com.example.newbiechen.nbreader.ui.component.book.util

import android.content.Context
import android.os.Environment
import com.example.newbiechen.nbreader.uilts.FileUtil
import java.io.File

/**
 *  author : newbiechen
 *  date : 2019-09-18 17:20
 *  description :解析书籍需要用到的文件路径
 */

object BookFileUtil {
    /**
     * 获取临时缓存目录
     */
    fun getPluginTempDir(context: Context): String {
        return FileUtil.getCachePath(context) + File.separator + "plugin"
    }
}