package com.example.newbiechen.nbreader.ui.component.book.util

import android.content.Context
import android.os.Environment
import com.example.newbiechen.nbreader.data.entity.BookEntity
import com.example.newbiechen.nbreader.uilts.FileUtil
import java.io.File

/**
 *  author : newbiechen
 *  date : 2019-09-18 17:20
 *  description :解析书籍需要用到的文件路径
 */

object BookFileUtil {

    /**
     * 获取插件缓存目录
     */
    fun getPluginCacheDir(context: Context): String {
        return FileUtil.getCachePath(context)
    }

}