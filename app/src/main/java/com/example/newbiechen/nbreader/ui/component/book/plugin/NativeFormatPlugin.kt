package com.example.newbiechen.nbreader.ui.component.book.plugin

import android.content.Context
import com.example.newbiechen.nbreader.data.entity.BookEntity
import com.example.newbiechen.nbreader.ui.component.book.BookModel
import com.example.newbiechen.nbreader.ui.component.book.type.BookType
import com.example.newbiechen.nbreader.ui.component.book.util.BookFileUtil
import com.example.newbiechen.nbreader.uilts.LogHelper
import java.io.File

/**
 *  author : newbiechen
 *  date : 2019-09-18 17:19
 *  description :原生解析插件
 */

open class NativeFormatPlugin(private val context: Context, private val bookType: BookType) {

    companion object {
        private const val TAG = "NativeFormatPlugin"

        /**
         * 获取缓存目录
         */
        fun getCacheDir(context: Context): String {
            return BookFileUtil.getPluginCacheDir(context)
        }

        /**
         * 获取书籍的缓存目录
         */
        fun getBookCacheDir(context: Context, bookEntity: BookEntity): String {
            return getCacheDir(context) + File.separator + bookEntity.title
        }
    }

    protected fun getContext() = context

    fun getSupportType() = bookType

    /**
     * 主要用于方便给 native 调用
     */
    fun getSupportTypeByStr() = bookType.toString().toLowerCase()

    fun readModel(model: BookModel) {

        val resultCode = readModelNative(
            model, getBookCacheDir(
                getContext(), model.book
            ), model.book.id
        )

        LogHelper.i(TAG, "resultCode:$resultCode")

        // TODO：之后单独创建一个 Exception
        if (resultCode != 0) {
            throw IllegalAccessError("read book model error")
        }
    }

    /**
     * @return  1 ==> 表示不支持该书本的解析格式
     *          2 ==> 表示插件解析错误
     *          3 ==>
     */
    private external fun readModelNative(
        bookModel: BookModel,
        cacheDir: String,
        cacheName: String
    ): Int

    // private external fun readMetaInfoNative(book: BookEntity): Int
}