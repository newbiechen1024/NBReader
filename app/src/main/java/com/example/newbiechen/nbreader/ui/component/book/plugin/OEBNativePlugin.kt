package com.example.newbiechen.nbreader.ui.component.book.plugin

import android.content.Context
import com.example.newbiechen.nbreader.data.entity.book.BookEntity
import com.example.newbiechen.nbreader.ui.component.book.type.BookType

/**
 *  author : newbiechen
 *  date : 2019-09-19 17:22
 *  description : epub、 格式解析器
 */

class OEBNativePlugin(context: Context, bookType: BookType) : NativeFormatPlugin(context, bookType) {
    override fun readMetaInfo(book: BookEntity) {

    }
}