package com.example.newbiechen.nbreader.ui.component.book

import com.example.newbiechen.nbreader.data.entity.BookEntity
import com.example.newbiechen.nbreader.ui.component.book.text.TextModel
import com.example.newbiechen.nbreader.ui.component.book.plugin.NativeFormatPlugin
import com.example.newbiechen.nbreader.ui.component.book.text.TextPlainModel
import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextParagraphInfo
import com.example.newbiechen.nbreader.uilts.LogHelper

/**
 *  author : newbiechen
 *  date : 2019-09-18 16:33
 *  description :书籍解析后的数据模型
 */

class BookModel private constructor(val book: BookEntity) {

    companion object {
        private const val TAG = "BookModel"
        fun createBookModel(book: BookEntity, plugin: NativeFormatPlugin): BookModel {
            val bookModel = BookModel(book)
            plugin.readModel(bookModel)
            return bookModel
        }
    }

    /**
     * Native 层通过该方法创建 TextModel 并赋值给 BookModel
     */
    fun createTextModel(
        id: String?,
        lang: String,
        paragraphBasePath: String,
        paragraphDetailPath: String
    ): TextModel {
        return TextPlainModel(id, lang, paragraphBasePath, paragraphDetailPath)
    }

    // TextModel 是在 native 层创建的，native 层调用 java 层赋值。
    var textModel: TextModel? = null
}