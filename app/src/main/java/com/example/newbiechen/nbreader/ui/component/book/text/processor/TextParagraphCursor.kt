package com.example.newbiechen.nbreader.ui.component.book.text.processor

import com.example.newbiechen.nbreader.ui.component.book.text.TextModel
import com.example.newbiechen.nbreader.ui.component.book.text.TextParagraph
import com.example.newbiechen.nbreader.ui.component.book.text.entity.entry.TextEntry
import com.example.newbiechen.nbreader.ui.component.book.text.entity.entry.TextParagraphEntry
import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextParagraphType
import com.example.newbiechen.nbreader.ui.component.book.text.entity.element.TextControlElement
import com.example.newbiechen.nbreader.ui.component.book.text.entity.element.TextElement
import com.example.newbiechen.nbreader.ui.component.book.text.entity.element.TextWordElement
import com.example.newbiechen.nbreader.ui.component.book.text.entity.entry.TextControlEntry
import com.example.newbiechen.nbreader.uilts.LogHelper
import kotlin.math.min

/**
 *  author : newbiechen
 *  date : 2019-10-21 17:03
 *  description :文本段落查找光标
 */

class TextParagraphCursor {

    // 当前索引，无法指向 Paragraph 的末尾，即其大小 < paragraphCount
    // 因为，当前不需要让 ParagraphCursor 指向末尾的能力
    val curParagraphIndex: Int

    private lateinit var mElementList: ArrayList<TextElement>

    private val mTextModel: TextModel
    // 段落光标管理器
    private val mCursorManager: TextCursorManager

    private var mParagraph: TextParagraph

    constructor(cursorManager: TextCursorManager, textModel: TextModel, index: Int) {
        mTextModel = textModel
        mCursorManager = cursorManager
        curParagraphIndex = min(index, textModel.getParagraphCount() - 1)
        mParagraph = mTextModel.getParagraph(curParagraphIndex)!!
        initElement()
    }

    private fun initElement() {
        // 通过解析器解析 TextParagraph 生成 TextElement 集合
        mElementList = TextEntryDecoder(mParagraph, mTextModel.getLanguage()).decode()
    }

    // 是否光标在起始位置
    fun isFirstParagraph(): Boolean {
        return curParagraphIndex == 0
    }

    // 是否光标指向最后一行
    fun isLastParagraph(): Boolean {
        return curParagraphIndex == (mTextModel.getParagraphCount() - 1)
    }

    fun isEndOfSection(): Boolean {
        return mTextModel.getParagraph(curParagraphIndex)!!.getInfo().type == TextParagraphType.END_OF_SECTION_PARAGRAPH
    }

    // 返回上一段落光标
    fun preCursor(): TextParagraphCursor? {
        return if (isFirstParagraph()) null else mCursorManager[curParagraphIndex - 1]
    }

    // 返回下一个段落光标
    fun nextCursor(): TextParagraphCursor? {
        return if (isLastParagraph()) null else mCursorManager[curParagraphIndex + 1]
    }

    // 获取当前段落光标
    fun getParagrah(): TextParagraph {
        return mParagraph
    }

    // 获取当前段落的文本元素
    fun getElement(index: Int): TextElement? {
        return if (index >= getElementCount()) {
            null
        } else {
            mElementList[index]
        }
    }

    fun getElementCount(): Int {
        return mElementList.size
    }
}

/**
 * TextEntry 解析器
 */
private class TextEntryDecoder(private val paragraph: TextParagraph, private val language: String) {

    companion object {
        private const val TAG = "TextEntryDecoder"

        private const val NO_SPACE = 0 // 没有空格
        private const val SPACE = 1 // 空格
        private const val NON_BREAKABLE_SPACE = 2 // 不间断空格

        // link break 缓冲区
        private var mBreakCacheArr: ByteArray = ByteArray(1024)
    }

    private var mElementList: ArrayList<TextElement>? = null
    // 当前段落偏移
    private var mParagraphOffset: Int = 0

    fun decode(): ArrayList<TextElement> {
        if (mElementList != null) {
            return mElementList!!
        }

        mElementList = ArrayList()

        // 获取当前段落
        when (paragraph.getInfo().type) {
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
                LogHelper.i(TAG, "else type:" + paragraph.getInfo().type)
            }
        }

        return mElementList!!
    }

    private fun processTextParagraph() {
        val entryIterator = paragraph.getIterator()
        var entry: TextParagraphEntry? = null

        while (entryIterator.hasNext()) {
            entry = entryIterator.next()

            // 如果拿到的数据为空，也 break
            if (entry == null) {
                LogHelper.i(TAG, "processTextParagraph empty")
                break
            }

            when (entry) {
                is TextEntry -> {
                    processTextEntry(entry)
                }
                is TextControlEntry -> {
                    // 创建一个 ControlElement
                    mElementList!!.add(
                        TextControlElement(
                            entry.style,
                            entry.isControlStart
                        )
                    )
                }
            }
        }
        LogHelper.i(TAG, "element count:" + mElementList!!.size)
    }

    private fun processTextEntry(textEntry: TextEntry) {
        textEntry.apply {
            // 如果文本长度为 0，则直接返回
            if (length == 0) {
                return
            }

            // 如果缓冲区小于长度，则创建一个新的缓冲区
            if (mBreakCacheArr.size < length) {
                mBreakCacheArr = ByteArray(length)
            }

            // 根据 textEntry 获取 breakBuffer
            LineBreaker.setLineBreak(data, offset, length, language, mBreakCacheArr)

            var spaceState: Int = NO_SPACE
            var wordStart: Int = 0
            var previousChar: Char
            var ch: Char = 0.toChar()

            for (index: Int in 0 until length) {
                previousChar = ch
                ch = data[offset + index]

                // 如果字符是空白字符，空白字符指的是(空格，制表符，tab垂直分隔)
                if (Character.isWhitespace(ch)) {
                    // 如果非起始位置，且状态为 NO_SPACE
                    if (index > 0 && spaceState == NO_SPACE) {
                        // 添加单词
                        addWord(
                            data, offset + wordStart,
                            index - wordStart,
                            mParagraphOffset + wordStart
                        )
                    }
                    spaceState = SPACE
                } else if (Character.isSpaceChar(ch)) {
                    // isWhitespace 和 isSpaceChar 表示两种不同的空格
                    // 如果之前不存在 SPACE 字符，则添加
                    if (index > 0 && spaceState == NO_SPACE) {
                        addWord(
                            data, offset + wordStart,
                            index - wordStart,
                            mParagraphOffset + wordStart
                        )
                    }

                    // 添加一个 NBSpace Element 到局部 elements 中 ==> 禁止换行
                    mElementList!!.add(TextElement.NBSpace)

                    // 设置当前状态 spaceState
                    if (spaceState != SPACE) {
                        spaceState = NON_BREAKABLE_SPACE
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
                            addWord(
                                data, offset + wordStart,
                                index - wordStart,
                                mParagraphOffset + wordStart
                            )
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
                    data,
                    offset + wordStart,
                    length - wordStart,
                    mParagraphOffset + wordStart
                )
            }

            // 总偏移位置
            mParagraphOffset += length
        }
    }

    // 添加单词
    private fun addWord(data: CharArray, offset: Int, len: Int, paragraphOffset: Int) {
        mElementList!!.add(TextWordElement(data, offset, len, paragraphOffset))
    }
}
