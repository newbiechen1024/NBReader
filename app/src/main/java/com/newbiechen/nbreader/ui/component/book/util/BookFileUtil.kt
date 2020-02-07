package com.newbiechen.nbreader.ui.component.book.util

import android.content.Context
import com.newbiechen.nbreader.uilts.FileUtil

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