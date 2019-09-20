package com.example.newbiechen.nbreader.ui.component.book.entity

import com.example.newbiechen.nbreader.ui.component.book.entity.text.TextModel
import com.example.newbiechen.nbreader.ui.component.book.plugin.NativeFormatPlugin

/**
 *  author : newbiechen
 *  date : 2019-09-18 16:33
 *  description :书籍解析后的数据模型
 */

class BookModel constructor(val book: Book) {

    companion object {
        fun createBookModel(book: Book, plugin: NativeFormatPlugin) {
            val bookModel = BookModel(book)
            plugin.readModel(bookModel)
        }
    }

    var textModel: TextModel? = null
}