package com.example.newbiechen.nbreader.ui.component.book.plugin

import android.content.Context
import com.example.newbiechen.nbreader.data.entity.book.BookEntity
import com.example.newbiechen.nbreader.ui.component.book.type.BookType
import com.example.newbiechen.nbreader.ui.component.book.util.BookFileUtil

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

abstract class FormatPlugin(context: Context, private val bookType: BookType) {

    private var tempDir: String = BookFileUtil.getPluginTempDir(context)

    fun getTempDir() = tempDir

    fun getSupportType() = bookType

    /**
     * 主要用于方便给 native 调用
     */
    fun getSupportTypeByStr() = bookType.toString().toLowerCase()

    // 获取书籍元数据信息
    abstract fun readMetaInfo(book: BookEntity)

/*    // 获取书籍封面信息
    abstract fun readCover()
    // 支持的编码格式
    abstract fun supportEncoding()*/
}