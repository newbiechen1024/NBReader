package com.example.newbiechen.nbreader.ui.component.book

import com.example.newbiechen.nbreader.data.entity.BookEntity
import com.example.newbiechen.nbreader.ui.component.book.text.TextModel
import com.example.newbiechen.nbreader.ui.component.book.plugin.NativeFormatPlugin
import com.example.newbiechen.nbreader.ui.component.book.text.TextPlainModel
import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextParagraphInfo

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

    /**
     * Native 层通过该方法创建 TextModel 并赋值给 BookModel
     */
    fun createTextModel(
        id: String,
        lang: String,
        bufferBlockCount: Int,
        cacheDir: String,
        fileExtension: String,
        // 每个段落的数据信息
        paragraphInfos: Array<TextParagraphInfo>
    ): TextModel {
        return TextPlainModel(
            id, lang, bufferBlockCount, cacheDir, fileExtension, paragraphInfos
        )
    }

    // TextModel 是在 native 层创建的，native 层调用 java 层赋值。
    var textModel: TextModel? = null
}