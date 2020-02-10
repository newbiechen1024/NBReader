package com.newbiechen.nbreader.ui.component.book.text.processor

import android.util.LruCache
import com.newbiechen.nbreader.ui.component.book.plugin.NativeFormatPlugin
import com.newbiechen.nbreader.ui.component.book.text.entity.TextChapter
import com.newbiechen.nbreader.ui.component.book.text.processor.cursor.TextChapterCursor
import java.lang.IndexOutOfBoundsException

/**
 *  author : newbiechen
 *  date : 2020-01-05 16:14
 *  description :章节加载模块
 *  TODO：暂时没想好，TextModel 当前定义为 LocalTextModel，之后应该会封装成一个接口，支持网络情况
 */

class TextModel(private val plugin: NativeFormatPlugin) {

    private val mChapterArr: Array<TextChapter>? = plugin.getChapters()

    // 章节光标缓冲
    private val mChapterCursorCache: LruCache<Int, TextChapterCursor> =
        object : LruCache<Int, TextChapterCursor>(5) {
            override fun create(key: Int?): TextChapterCursor {
                return TextChapterCursor(this@TextModel, key!!)
            }
        }

    /**
     * 获取文本的语言
     */
    fun getLanguage() = plugin.getLanguage()

    /**
     * 获取章节信息
     */
    fun getChapter(index: Int): TextChapter {
        if (index < 0 || index >= getChapterCount()) {
            throw IndexOutOfBoundsException()
        }
        return mChapterArr!![index]
    }

    /**
     * 获取章节的内容
     */
    fun getChapterContent(index: Int): ByteArray? {
        // 应该获取的是
        return plugin.getChapterContent(getChapter(index))
    }

    /**
     * 获取章节光标
     * @param index：章节索引
     */
    fun getChapterCursor(index: Int): TextChapterCursor {
        if (index < 0 || index >= getChapterCount()) {
            throw IndexOutOfBoundsException()
        }
        return mChapterCursorCache[index]
    }

    fun getChapterCount(): Int {
        return mChapterArr?.size ?: 0
    }
}