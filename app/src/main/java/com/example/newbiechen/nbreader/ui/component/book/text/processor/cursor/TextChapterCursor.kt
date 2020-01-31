package com.example.newbiechen.nbreader.ui.component.book.text.processor.cursor

import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextParagraph
import com.example.newbiechen.nbreader.ui.component.book.text.entity.tag.*
import com.example.newbiechen.nbreader.ui.component.book.text.processor.TextModel
import com.example.newbiechen.nbreader.ui.component.book.text.util.ByteToDataUtil
import com.example.newbiechen.nbreader.uilts.LogHelper
import java.lang.IndexOutOfBoundsException

/**
 *  author : newbiechen
 *  date : 2020-01-05 16:26
 *  description:章节光标
 */

class TextChapterCursor(private val textModel: TextModel, private val chapterIndex: Int) {

    // 章节中的段落信息列表
    private var mTextParagraphList: ArrayList<TextParagraph> = ArrayList()

    // 段落光标缓存
    private var mTextParagraphCursorCache: HashMap<Int, TextParagraphCursor> = HashMap()

    // 章节中包含的所有标签
    private var mTextTagList: ArrayList<TextTag>? = null

    // 章节信息
    private var mChapter = textModel.getChapter(chapterIndex)

    companion object {
        private const val TAG = "TextChapterCursor"
    }

    init {
        // TODO:关于获取不到数据的错误处理，之后再说
        // 创建解析器解析
        mTextTagList = ChapterContentDecoder(
            textModel.getChapterContent(chapterIndex)!!
        ).decode()

        LogHelper.i(TAG, "init: ${mTextTagList!!.size}")

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
        return if (hasPrevChapter()) {
            textModel.getChapterCursor(chapterIndex - 1)
        } else {
            null
        }
    }

    private fun hasPrevChapter(): Boolean {
        return chapterIndex - 1 >= 0
    }

    /**
     * 获取下一个章节光标
     */
    fun nextCursor(): TextChapterCursor? {
        return if (hasNextChapter()) {
            textModel.getChapterCursor(chapterIndex + 1)
        } else {
            null
        }
    }

    private fun hasNextChapter(): Boolean {
        return chapterIndex + 1 < textModel.getChapterCount()
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

        var textParagraphCursor = mTextParagraphCursorCache[index]

        if (textParagraphCursor == null) {
            textParagraphCursor = TextParagraphCursor(this, index)
            mTextParagraphCursorCache[index] = textParagraphCursor
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
 * 章节内容解析器
 */
private class ChapterContentDecoder(private val chapterContent: ByteArray) {

    // 缓存区的偏移
    private var mBufferOffset = 0

    companion object {
        private const val TAG = "ChapterContentDecoder"
    }

    /**
     * 进行解析操作
     */
    fun decode(): ArrayList<TextTag> {
        // 重置
        mBufferOffset = 0

        val textTags = ArrayList<TextTag>()

        val bufferLen = chapterContent.size

        while (mBufferOffset < bufferLen) {

            // 获取当前索引下的标签类型
            val tagType = readTagType()

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
            }

            textTags.add(textTag!!)
        }

        return textTags
    }

    // 读取标签类型
    private fun readTagType(): Byte {
        // 从缓冲区中获取 tag 的类型
        var tagType = chapterContent[mBufferOffset]
        // tag 类型后，是填充对齐类型占 1 字节，所以需要偏移 2 字节。
        mBufferOffset += 2
        return tagType
    }

    /**
     *  如果标签类型是文本类型
     * TEXT_TAG：占用 (6 + 文本字节数) 格式为 | tag 类型 | 未知类型 | 文本字节长度 | 文本内容
     *
     * 1. tag 类型：占用 1 字节。(native 中 char 类型)
     * 2. 未知类型：占用 1 字节 ==> 基本上为 0 好像没用过 (native 中 char 类型)
     * 3. 文本字节长度：占用 4 字节。(native 中 uint 类型)
     * 4. 文本内容：具体文本内容。(当前采用 utf-16 编码)
     */
    private fun readContentTag(): TextContentTag {
        // 获取文本长度字节数组
        val textLengthArr = chapterContent.copyOfRange(mBufferOffset, mBufferOffset + 4)

        // 进行偏移操作
        mBufferOffset += 4

        // 文本字节长度
        var textLength = ByteToDataUtil.readUInt32(textLengthArr)

        // 文本内容
        val textContent =
            String(chapterContent, mBufferOffset, textLength.toInt(), Charsets.UTF_16LE)

        // 读取文本数据，并对 block 进行偏移
        mBufferOffset += textLength.toInt()

        return TextContentTag(textContent)
    }

    /**
     * control tag 结构：占用 4 字节，格式为 | tag 类型 | 0 | 控制标签 | 是开放标签还是闭合标签 |
     *
     * 1. tag 类型：占用 1 字节
     * 2. 未知类型：占用 1 字节 ==> 基本上为 0 好像没用过
     * 3. 控制位类型：占用 1 字节 ==> 详见 TextParagraph::Type
     * 4. 标签类型：占用 1 字节 ==> 0 或者是 1
     */
    private fun readControlTag(): TextControlTag {
        // 获取 control 类型，并进行偏移
        val controlType = chapterContent[mBufferOffset++]
        // 判断是起始标签，还是结尾标签
        var isControlStart = ByteToDataUtil.readBoolean(chapterContent[mBufferOffset++])

        return TextControlTag(controlType, isControlStart)
    }

    /**
     * paragraph tag 结构：占用 4 字节，格式为 | tag 类型 | 0 | 段落标签 | 0
     * 1. tag 类型：占用 1 字节。
     * 2. 未知类型：占用 1 字节。 ==> 基本上为 0 好像没用过
     * 3. 段落类型：占用 1 字节。 ==> 详见 TextParagraph::Type
     * 4. 填充对齐：占用 1 字节。
     */
    private fun readParagraphTag(): TextParagraphTag {
        // 获取 control 类型，并进行偏移
        val paragraphType = chapterContent[mBufferOffset]
        // 偏移操作
        mBufferOffset += 2
        return TextParagraphTag(paragraphType)
    }
}

