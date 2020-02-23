package com.newbiechen.nbreader.ui.component.book.plugin

import com.newbiechen.nbreader.ui.component.book.text.entity.TextChapter
import com.newbiechen.nbreader.ui.component.book.type.BookType
import java.io.File

/**
 *  author : newbiechen
 *  date : 2019-09-18 17:19
 *  description :原生解析插件
 */

open class NativeFormatPlugin(private val bookType: BookType) {

    private val mNativePluginDesc: Int
    // 书籍路径
    private var mBookPath: String? = null

    init {
        // 在 native 层创建插件，获取插件描述符
        mNativePluginDesc = createFormatPluginNative(bookType.name.toLowerCase())
    }

    /**
     * 根据本地书籍路径打开书籍
     */
    fun openBook(bookPath: String) {
        // 如果是已经添加过的路径，就不要重复添加
        if (bookPath == mBookPath) {
            return
        }

        mBookPath = bookPath

        // 检测书籍路径是否正确
        setBookSourceNative(mNativePluginDesc, bookPath)
    }

    /**
     * 传入书籍组信息，用于处理书籍中有多个单一的章节文件，通过自定义 TextChapter 实现解析的逻辑
     */
    fun openBook(bookGroup: BookGroup) {
        // TODO：暂未实现
    }

    fun setConfigure(cachePath: String, chapterPattern: String, chapterPrologueTitle: String) {
        val cacheDir = File(cachePath)

        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }

        check(cacheDir.exists()) {
            "please input right cachePath"
        }

        setConfigureNative(mNativePluginDesc, cachePath, chapterPattern, chapterPrologueTitle)
    }

    @Synchronized
    fun getEncoding(): String {
        return getEncodingNative(mNativePluginDesc)
    }

    @Synchronized
    fun getLanguage(): String {
        return getLanguageNative(mNativePluginDesc)
    }

    @Synchronized
    fun getChapters(): Array<TextChapter>? {
        return getChaptersNative(mNativePluginDesc)
    }

    fun getChapterContent(chapter: TextChapter): ByteArray? {
        return readChapterContentNative(mNativePluginDesc, chapter)
    }

    fun release() {
        releaseFormatPluginNative(mNativePluginDesc)
    }

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