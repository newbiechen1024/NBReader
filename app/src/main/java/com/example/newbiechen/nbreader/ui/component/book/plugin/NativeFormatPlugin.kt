package com.example.newbiechen.nbreader.ui.component.book.plugin

import android.content.Context
import com.example.newbiechen.nbreader.data.entity.BookEntity
import com.example.newbiechen.nbreader.ui.component.book.entity.BookModel
import com.example.newbiechen.nbreader.ui.component.book.type.BookType

/**
 *  author : newbiechen
 *  date : 2019-09-18 17:19
 *  description :原生解析插件
 */

open class NativeFormatPlugin(context: Context, bookType: BookType) : FormatPlugin(context, bookType) {

    override fun readMetaInfo(book: BookEntity) {

    }

    fun readModel(model: BookModel) {

    }

    /**
     * @return  1 ==> 表示不支持该书本的解析格式
     *          2 ==> 表示插件解析错误
     *          3 ==>
     *
     */
    // private external fun readModelNative(bookModel: BookModel, cacheDir: String): Int
}