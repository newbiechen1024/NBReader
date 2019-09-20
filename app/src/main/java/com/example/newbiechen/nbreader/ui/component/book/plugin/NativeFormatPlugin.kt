package com.example.newbiechen.nbreader.ui.component.book.plugin

import android.content.Context
import com.example.newbiechen.nbreader.ui.component.book.entity.Book
import com.example.newbiechen.nbreader.ui.component.book.entity.BookModel
import com.example.newbiechen.nbreader.ui.component.book.type.BookType

/**
 *  author : newbiechen
 *  date : 2019-09-18 17:19
 *  description :原生解析插件
 */

open class NativeFormatPlugin(context: Context, bookType: BookType) : FormatPlugin(context, bookType) {

    override fun readMetaInfo(book: Book) {

    }

    fun readModel(model: BookModel) {

    }


}