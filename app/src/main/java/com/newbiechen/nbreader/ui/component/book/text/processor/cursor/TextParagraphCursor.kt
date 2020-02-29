package com.newbiechen.nbreader.ui.component.book.text.processor.cursor

import com.newbiechen.nbreader.ui.component.book.text.entity.TextParagraph
import com.newbiechen.nbreader.ui.component.book.text.entity.TextPosition
import com.newbiechen.nbreader.ui.component.book.text.entity.element.*
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.*
import com.newbiechen.nbreader.ui.component.book.text.processor.TextModel
import com.newbiechen.nbreader.ui.component.book.text.util.LineBreaker
import com.newbiechen.nbreader.uilts.LogHelper

/**
 *  author : newbiechen
 *  date : 2019-10-21 17:03
 *  description :文本段落查找光标
 */

class TextParagraphCursor : TextPosition {

    private var mElementList: ArrayList<TextElement>

    // 该段落对应的章节
    private val mChapterCursor: TextChapterCursor

    // 文本信息模块
    private var mTextModel: TextModel

    // 段落索引
    private var mParagraphIndex: Int

    // 段落信息
    private var mParagraph: TextParagraph

    /**
     * @param chapterCursor：章节对应的段落列表
     * @param index：章节列表在段落中的位置
     */
    constructor(chapterCursor: TextChapterCursor, index: Int = 0) {
        mChapterCursor = chapterCursor
        mParagraphIndex = index
        mParagraph = chapterCursor.getParagraph(index)
        mTextModel = chapterCursor.getChapterModel()

        // 将段落的 tag 元素解析成 element 元素
        mElementList = ParagraphContentDecoder(
            mParagraph,
            chapterCursor.getParagraphContent(index),
            mTextModel.getLanguage()
        ).decode()
    }

    /**
     * 是否是章节中的第一段
     */
    fun isFirstOfChapter(): Boolean {
        return getParagraphIndex() == 0
    }

    /**
     * 是否是书中的第一段
     */
    fun isFirstOfText(): Boolean {
        return mChapterCursor.isFirstChapter() && isFirstOfChapter()
    }

    /**
     * 是否是章节中的最后一段
     */
    fun isLastOfChapter(): Boolean {
        return getParagraphIndex() == mChapterCursor.getParagraphCount() - 1
    }

    /**
     * 是否是书中的最后一段
     */
    fun isLastOfText(): Boolean {
        return mChapterCursor.isLastChapter() && isLastOfChapter()
    }

    /**
     * 检测段落是否是片段结束段落。该段落是片段的最后一段。
     * 作用：表示之后的内容需要开启新的页面显示。
     */
    fun isEndOfSection(): Boolean {
        return mParagraph.type == TextParagraphType.END_OF_SECTION_PARAGRAPH
    }

    // TODO:如果存在 chapterCursor 存在空章节的情况，是否会造成问题，之后考虑

    /**
     * 返回上一段落光标
     */
    fun prevCursor(): TextParagraphCursor? {
        // 检测段落是否是书籍的起始段
        return if (!isFirstOfText()) {
            // 检测是否是章节的第一段
            if (isFirstOfChapter()) {
                val prevChapterCursor = mChapterCursor.prevCursor()!!
                prevChapterCursor.getParagraphCursor(prevChapterCursor.getParagraphCount() - 1)
            } else {
                mChapterCursor.getParagraphCursor(getParagraphIndex() - 1)
            }
        } else {
            null
        }
    }

    /**
     * 返回下一个段落光标
     */
    fun nextCursor(): TextParagraphCursor? {
        return if (!isLastOfText()) {
            // 检测是否是章节的最后一段
            if (isLastOfChapter()) {
                val nextChapterCursor = mChapterCursor.nextCursor()!!
                nextChapterCursor.getParagraphCursor(0)
            } else {
                mChapterCursor.getParagraphCursor(getParagraphIndex() + 1)
            }
        } else {
            null
        }
    }

    /**
     * 获取当前段落
     */
    fun getParagrah(): TextParagraph {
        return mParagraph
    }

    /**
     * 获取当前段落的文本元素
     */
    fun getElement(index: Int): TextElement? {
        return if (index >= getElementCount()) {
            null
        } else {
            mElementList[index]
        }
    }

    /**
     * 获取段落中的元素总数
     */
    fun getElementCount(): Int {
        return mElementList.size
    }

    fun getChapterCursor(): TextChapterCursor {
        return mChapterCursor
    }

    override fun getChapterIndex(): Int {
        return mChapterCursor.getChapterIndex()
    }

    override fun getParagraphIndex(): Int {
        return mParagraphIndex
    }

    override fun getElementIndex(): Int {
        return 0
    }

    override fun getCharIndex(): Int {
        return 0
    }
}

/**
 * 段落内容解析器
 */
private class ParagraphContentDecoder(
    private val paragraph: TextParagraph,
    private val paragraphContent: TextTagIterator,
    private val language: String
) {

    companion object {
        private const val TAG = "ParagraphContentDecoder"

        private const val NO_SPACE = 0 // 没有空格
        private const val SPACE = 1 // 空格
        private const val NON_BREAKABLE_SPACE = 2 // 不间断空格

        // link break 缓冲区
        private var mBreakCacheArr: ByteArray = ByteArray(1024)
    }

    private var mElementList: ArrayList<TextElement>? = null

    fun decode(): ArrayList<TextElement> {
        if (mElementList != null) {
            return mElementList!!
        }

        mElementList = ArrayList()

        // 获取当前段落
        when (paragraph.type) {
            TextParagraphType.TEXT_PARAGRAPH -> {
                processTextParagraph()
            }
            TextParagraphType.EMPTY_LINE_PARAGRAPH -> {
                // 空行段落
            }
            TextParagraphType.ENCRYPTED_SECTION_PARAGRAPH -> {
                // 不处理
            }
            else -> {
                LogHelper.i(TAG, "else type:" + paragraph.type)
            }
        }
        return mElementList!!
    }

    private fun processTextParagraph() {
        val tagIterator = paragraphContent
        var textTag: TextTag

        while (tagIterator.hasNext()) {
            textTag = tagIterator.next()
            when (textTag) {
                is TextContentTag -> {
                    processContentTag(textTag)
                }
                is TextControlTag -> {
                    processControlTag(textTag)
                }
                is TextStyleTag -> {
                    processStyleTag(textTag)
                }
                is TextStyleCloseTag -> {
                    processStyleCloseTag(textTag)
                }
                is TextFixedHSpaceTag -> {
                    processFixedHSpaceTag(textTag)
                }
                is TextImageTag -> {
                    processImageTag(textTag)
                }
            }
        }

        LogHelper.i(
            TAG,
            "element count:" + mElementList!!.size
        )
    }

    private fun processContentTag(textTag: TextContentTag) {
        textTag.apply {
            if (content.isEmpty()) {
                return
            }

            val contentArr = content.toCharArray()
            val contentLen = contentArr.size

            // 如果缓冲区小于长度，则创建一个新的缓冲区
            if (mBreakCacheArr.size < contentLen) {
                mBreakCacheArr = ByteArray(contentLen)
            }

            // 根据 textEntry 获取 breakBuffer
            LineBreaker.setLineBreak(
                contentArr,
                0,
                contentLen,
                language,
                mBreakCacheArr
            )

            var spaceState: Int = NO_SPACE
            var wordStart: Int = 0
            var previousChar: Char
            var ch: Char = 0.toChar()

            for (index: Int in 0 until contentLen) {
                previousChar = ch
                ch = contentArr[index]

                // 如果字符是空白字符，空白字符指的是(空格，制表符，tab垂直分隔)
                if (Character.isWhitespace(ch)) {
                    // 如果非起始位置，且状态为 NO_SPACE
                    if (index > 0 && spaceState == NO_SPACE) {
                        // 添加单词
                        addWord(contentArr, wordStart, index - wordStart)
                    }
                    spaceState =
                        SPACE
                } else if (Character.isSpaceChar(ch)) {
                    // isWhitespace 和 isSpaceChar 表示两种不同的空格
                    // 如果之前不存在 SPACE 字符，则添加
                    if (index > 0 && spaceState == NO_SPACE) {
                        addWord(
                            contentArr, wordStart, index - wordStart
                        )
                    }

                    // 添加一个 NBSpace Element 到局部 elements 中 ==> 禁止换行
                    mElementList!!.add(TextElement.NBSpace)

                    // 设置当前状态 spaceState
                    if (spaceState != SPACE) {
                        spaceState =
                            NON_BREAKABLE_SPACE
                    }
                } else {
                    // 如果字符非空白字符
                    // 如果当前是 Space 状态
                    when (spaceState) {
                        SPACE -> {
                            mElementList!!.add(TextElement.HSpace)
                            // 将 index 设置为 word 的起始位置
                            wordStart = index
                        }
                        NON_BREAKABLE_SPACE -> wordStart = index
                        // 添加非 Break 字符
                        NO_SPACE -> if (index > 0 &&
                            mBreakCacheArr[index - 1] != LineBreaker.NOBREAK &&
                            previousChar != '-' && index != wordStart
                        ) {
                            addWord(contentArr, wordStart, index - wordStart)
                            wordStart = index
                        }
                    }
                    spaceState = NO_SPACE
                }
            }

            // 对最后字符的处理
            when (spaceState) {
                SPACE -> mElementList!!.add(TextElement.HSpace)
                NON_BREAKABLE_SPACE -> mElementList!!.add(TextElement.NBSpace)
                NO_SPACE -> addWord(
                    contentArr,
                    wordStart,
                    contentLen - wordStart
                )
            }
        }
    }

    // 添加单词
    private fun addWord(data: CharArray, offset: Int, len: Int) {
        mElementList!!.add(TextWordElement(data, offset, len))
    }

    // 创建一个 ControlElement
    private fun processControlTag(textTag: TextControlTag) {
        mElementList!!.add(
            TextControlElement(
                textTag.type,
                textTag.isControlStart
            )
        )
    }

    private fun processStyleTag(textTag: TextStyleTag) {
        mElementList!!.add(TextStyleElement(textTag))
    }

    private fun processStyleCloseTag(textTag: TextStyleCloseTag) {
        mElementList!!.add(
            TextElement.StyleClose
        )
    }

    private fun processFixedHSpaceTag(textFixedHSpaceTag: TextFixedHSpaceTag) {
        mElementList!!.add(
            TextFixedHSpaceElement.getElement(textFixedHSpaceTag.length)
        )
    }

    private fun processImageTag(textTag: TextImageTag) {
        // 添加图片元素
        mElementList!!.add(TextImageElement(textTag.textImage))
    }
}
