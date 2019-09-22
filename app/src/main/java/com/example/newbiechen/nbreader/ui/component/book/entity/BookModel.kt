package com.example.newbiechen.nbreader.ui.component.book.entity

import com.example.newbiechen.nbreader.data.entity.BookEntity
import com.example.newbiechen.nbreader.ui.component.book.entity.text.TextModel
import com.example.newbiechen.nbreader.ui.component.book.plugin.NativeFormatPlugin

/**
 *  author : newbiechen
 *  date : 2019-09-18 16:33
 *  description :书籍解析后的数据模型
 */

class BookModel private constructor(val book: BookEntity) {

    companion object {
        fun createBookModel(book: BookEntity, plugin: NativeFormatPlugin): BookModel {
            val bookModel = BookModel(book)
            plugin.readModel(bookModel)
            return bookModel
        }
    }

    var textModel: TextModel? = null
}