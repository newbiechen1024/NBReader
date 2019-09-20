package com.example.newbiechen.nbreader.ui.component.book.entity

import com.example.newbiechen.nbreader.ui.component.book.plugin.FormatPlugin
import com.example.newbiechen.nbreader.ui.component.book.plugin.NativeFormatPlugin

/**
 *  author : newbiechen
 *  date : 2019-09-18 16:33
 *  description :书籍解析后的数据模型
 */

class BookModel constructor(private val book: Book, private val plugin: NativeFormatPlugin) {

    init {
        // 调用 Plugin 初始化 model
        // TODO:放在主线程是否好 ==> 暂时不管
        plugin.readModel(this)
    }

}