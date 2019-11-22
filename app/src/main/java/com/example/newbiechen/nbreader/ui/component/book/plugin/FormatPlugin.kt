package com.example.newbiechen.nbreader.ui.component.book.plugin

import android.content.Context
import com.example.newbiechen.nbreader.data.entity.BookEntity
import com.example.newbiechen.nbreader.ui.component.book.type.BookType
import com.example.newbiechen.nbreader.ui.component.book.util.BookFileUtil
import java.io.File

/**
 *  author : newbiechen
 *  date : 2019-09-18 16:51
 *  description :插件接口类
 *
 *  子类包含：
 *
 *  1. NativeFormatPlugin:原生插件
 *  2. ExternalFormatPlugin:外置插件 ==> ExternalFormatPlugin 暂时用不到
 */

abstract class FormatPlugin(private val context: Context, private val bookType: BookType) {


}