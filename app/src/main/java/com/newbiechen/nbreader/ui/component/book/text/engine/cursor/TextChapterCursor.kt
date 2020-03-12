package com.newbiechen.nbreader.ui.component.book.text.engine.cursor

import android.util.LruCache
import com.newbiechen.nbreader.ui.component.book.text.entity.TextParagraph
import com.newbiechen.nbreader.ui.component.book.text.entity.resource.TextResource
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.*
import com.newbiechen.nbreader.ui.component.book.text.parcel.TextParcel
import com.newbiechen.nbreader.ui.component.book.text.engine.TextModel
import com.newbiechen.nbreader.ui.component.widget.page.PageType
import com.newbiechen.nbreader.uilts.LogHelper

/**
 *  author : newbiechen
 *  date : 2020-01-05 16:26
 *  description:章节光标
 */

class TextChapterCursor(private val textModel: TextModel, private val chapterIndex: Int) {

    // 章节中的段落信息列表
    private var mTextParagraphList: ArrayList<TextParagraph> = ArrayList()

    // 章节资源信息
    private var mTextResource: TextResource

    // 段落光标缓存 (200 最大值是自定义，可根据情况修改)
    private var mTextParagraphCursorCache: LruCache<Int, TextParagraphCursor?> = LruCache(200)

    // 章节中包含的所有标签
    private var mTextTagList: ArrayList<TextTag>? = null

    // 章节信息
    private var mChapter = textModel.getChapter(chapterIndex)

    companion object {
        private const val TAG = "TextChapterCursor"
    }

    init {
        // TODO:关于获取不到 content 的错误处理，之后再说
        // 文本内容信息
        val textContent = textModel.getChapterContent(chapterIndex)!!

        // 获取文本资源信息
        mTextResource = TextResource(textContent.resourceData)

        // 创建解析器解析
        mTextTagList = TextContentDecoder(textContent.contentData).decode()

        // TODO:原理是 paragraph tag 一定是在段落的末尾的。之后会修改 native 将 paragraph 放在起始位置
        // 解析成功后，取出 ParagraphTag 转换成 TextParagraph
        mTextTagList!!.forEachIndexed { index, textTag ->

            // 如果不是 paragraph 类型，直接返回 true
            if (textTag !is TextParagraphTag) {
                return@forEachIndexed
            }

            var lastTextParagraph: TextParagraph? = null

            if (mTextParagraphList.isNotEmpty()) {
                lastTextParagraph = mTextParagraphList.last()
            }

            mTextParagraphList.add(
                TextParagraph(
                    textTag.type,
                    mTextParagraphList.size,
                    lastTextParagraph?.endOffset ?: 0,
                    index
                )
            )
        }
    }

    fun getResource() = mTextResource

    /**
     * 获取章节索引
     */
    fun getChapterIndex() = chapterIndex

    /**
     * 获取章节
     */
    fun getChapter() = mChapter

    /**
     * 获取上一个章节光标
     */
    fun prevCursor(): TextChapterCursor? {
        return if (hasChapter(PageType.PREVIOUS)) {
            textModel.getChapterCursor(chapterIndex - 1)
        } else {
            null
        }
    }

    fun hasChapter(type: PageType): Boolean {
        return when (type) {
            PageType.PREVIOUS -> {
                chapterIndex - 1 >= 0
            }
            PageType.NEXT -> {
                chapterIndex + 1 < textModel.getChapterCount()
            }
            PageType.CURRENT -> {
                true
            }
        }
    }

    /**
     * 获取下一个章节光标
     */
    fun nextCursor(): TextChapterCursor? {
        return if (hasChapter(PageType.NEXT)) {
            textModel.getChapterCursor(chapterIndex + 1)
        } else {
            null
        }
    }

    /**
     * 是否是第一章
     */
    fun isFirstChapter(): Boolean {
        return chapterIndex == 0
    }

    /**
     * 是否是最后一章
     */
    fun isLastChapter(): Boolean {
        return chapterIndex == (textModel.getChapterCount() - 1)
    }

    /**
     * 获取章节中段落总数
     */
    fun getParagraphCount(): Int = mTextParagraphList.size

    /**
     * 获取章节中具体段落信息
     */
    fun getParagraph(index: Int): TextParagraph {
        if (index >= getParagraphCount()) {
            throw IndexOutOfBoundsException("paragraph index out of chapter paragraph count")
        }
        return mTextParagraphList[index]
    }

    /**
     * 获取段落内容
     */
    fun getParagraphContent(index: Int): TextTagIterator {
        return TextTagIteratorImpl(index)
    }

    /**
     * 获取章节中段落光标索引
     */
    fun getParagraphCursor(index: Int): TextParagraphCursor {
        if (index >= getParagraphCount()) {
            throw IndexOutOfBoundsException("paragraph index out of chapter paragraph count")
        }

        var textParagraphCursor = mTextParagraphCursorCache.get(index)

        if (textParagraphCursor == null) {
            textParagraphCursor = TextParagraphCursor(this, index)
            mTextParagraphCursorCache.put(index, textParagraphCursor)
        }

        return textParagraphCursor
    }

    fun getChapterModel() = textModel

    inner class TextTagIteratorImpl(
        paragraphIndex: Int
    ) : TextTagIterator {

        private val paragraph = getParagraph(paragraphIndex)
        private val endOffset = paragraph.endOffset

        private var curOffset = paragraph.startOffset

        override fun hasNext(): Boolean {
            return curOffset < endOffset
        }

        override fun next(): TextTag {
            return mTextTagList!![curOffset++]
        }

        override fun reset() {
            curOffset = paragraph.startOffset
        }
    }
}

/**
 * 文本内容解析器
 */
private class TextContentDecoder(contentData: ByteArray) {
    // 缓存区的偏移
    private var mBufferLength = contentData.size
    private var mParcel: TextParcel = TextParcel(contentData)

    companion object {
        private const val TAG = "TextContentDecoder"
    }

    fun decode(): ArrayList<TextTag> {
        val textTags = ArrayList<TextTag>()

        while (mParcel.offset() < mBufferLength) {

            // 获取当前索引下的标签类型
            val tagType = mParcel.readByte()

            // 生成对应的 TextTag
            var textTag: TextTag? = null

            when (tagType) {
                TextTagType.TEXT -> {
                    textTag = readContentTag()
                }
                TextTagType.CONTROL -> {
                    textTag = readControlTag()
                }
                TextTagType.PARAGRAPH -> {
                    // 处理段落标签
                    textTag = readParagraphTag()
                }
                TextTagType.STYLE_CSS,
                TextTagType.STYLE_OTHER -> {
                    textTag = readStyleTag(tagType)
                }
                TextTagType.STYLE_CLOSE -> {
                    textTag = readStyleCloseTag()
                }
                TextTagType.FIXED_HSPACE -> {
                    textTag = readFixedHSpaceTag()
                }
                TextTagType.IMAGE -> {
                    textTag = readImageTag()
                }
                TextTagType.HYPERLINK_CONTROL -> {
                    // TODO:未实现占位
                    textTag = readHyperlinkControlTag()
                }
            }
            LogHelper.i(TAG, "decode: $tagType")

            textTags.add(textTag!!)
        }

        return textTags
    }

    private fun readContentTag(): TextContentTag {
        return TextContentTag(mParcel)
    }

    private fun readControlTag(): TextControlTag {
        return TextControlTag(mParcel)
    }

    private fun readParagraphTag(): TextParagraphTag {
        return TextParagraphTag(mParcel)
    }

    private fun readStyleTag(type: Byte): TextStyleTag {
        // 创建样式标签
        return if (type == TextTagType.STYLE_CSS) {
            TextCssStyleTag(mParcel)
        } else {
            TextOtherStyleTag(mParcel)
        }
    }

    private fun readStyleCloseTag(): TextTag {
        return TextTag.StyleCloseTag
    }

    private fun readFixedHSpaceTag(): TextTag {
        return TextFixedHSpaceTag(mParcel)
    }

    private fun readImageTag(): TextTag {
        return TextImageTag(mParcel)
    }

    private fun readHyperlinkControlTag(): TextTag {
        return TextHyperlinkControlTag()
    }
}