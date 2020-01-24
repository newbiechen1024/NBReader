package com.example.newbiechen.nbreader.ui.component.book.plugin

import android.content.Context
import com.example.newbiechen.nbreader.data.entity.BookEntity
import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextChapter
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

    private val mNativePluginDesc: Int

    init {
        // 在 native 层创建插件，获取插件描述符
        mNativePluginDesc = createFormatPluginNative(bookType.name)
    }

    fun setBookResource(bookPath: String) {
        // 检测书籍路径是否正确
        setBookSourceNative(mNativePluginDesc, bookPath)
    }

    fun setConfigure(cachePath: String, chapterPattern: String, chapterPrologueTitle: String) {
        setConfigureNative(mNativePluginDesc, cachePath, chapterPattern, chapterPrologueTitle)
    }

    fun getEncoding(): String {
        return getEncodingNative(mNativePluginDesc)
    }

    fun getLanguage(): String {
        return getLanguageNative(mNativePluginDesc)
    }

    fun getChapters(): Array<TextChapter>? {
        return getChaptersNative(mNativePluginDesc)
    }

    fun getChapterContent(chapter: TextChapter): ByteArray? {
        return readChapterContentNative(mNativePluginDesc, chapter)
    }

    protected fun getContext() = context

    // 返回插件类型
    fun getPluginType() = bookType

    // private external fun readMetaInfoNative(book: BookEntity): Int

    protected fun finalize() {
        releaseFormatPluginNative(mNativePluginDesc)
    }

    /**
     * 插件文本编码插件
     */
    private external fun createFormatPluginNative(formatType: String): Int

    /**
     * 配置插件参数
     */
    private external fun setConfigureNative(
        pluginDesc: Int,
        cachePath: String,
        chapterPattern: String,
        chapterPrologueTitle: String
    )

    /**
     * 设置待处理的书籍
     */
    private external fun setBookSourceNative(pluginDesc: Int, bookPath: String)

    // TODO:如何将错误信息通知给上层？

    /**
     * 获取书籍的文字编码
     */
    private external fun getEncodingNative(pluginDesc: Int): String

    // 读取语言
    private external fun getLanguageNative(pluginDesc: Int): String

    // 读取章节
    private external fun getChaptersNative(pluginDesc: Int): Array<TextChapter>?

    // 读取章节内容，返回章节内容数组
    private external fun readChapterContentNative(
        pluginDesc: Int,
        chapter: TextChapter
    ): ByteArray?

    /**
     * 释放插件
     */
    private external fun releaseFormatPluginNative(pluginDesc: Int)
}