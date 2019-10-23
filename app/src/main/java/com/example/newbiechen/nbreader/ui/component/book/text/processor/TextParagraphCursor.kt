package com.example.newbiechen.nbreader.ui.component.book.text.processor

import com.example.newbiechen.nbreader.ui.component.book.text.TextModel
import com.example.newbiechen.nbreader.ui.component.book.text.TextParagraph
import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextEntry
import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextParagraphEntry
import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextParagraphType
import com.example.newbiechen.nbreader.ui.component.book.text.entity.element.TextElement
import com.example.newbiechen.nbreader.ui.component.book.text.entity.element.TextWordElement
import kotlin.math.min

/**
 *  author : newbiechen
 *  date : 2019-10-21 17:03
 *  description :文本段落查找光标
 */

class TextParagraphCursor {

    companion object {
        private const val NO_SPACE = 0 // 没有空格
        private const val SPACE = 1 // 空格
        private const val NON_BREAKABLE_SPACE = 2 // 不间断空格
    }

    private val mElementList = ArrayList<TextElement>()

    private val mTextModel: TextModel

    private val mParagraphCursor: TextCursorManager
    // 当前段落的索引
    private var mCurParagraphIndex: Int = 0

    private var mParagraph: TextParagraph

    constructor(cursorManager: TextCursorManager, textModel: TextModel, index: Int) {
        mTextModel = textModel
        mParagraphCursor = cursorManager
        mCurParagraphIndex = min(index, textModel.getParagraphCount() - 1)
        mParagraph = mTextModel.getParagraph(mCurParagraphIndex)!!
        initElement()
    }

    private fun initElement() {
        // 获取当前段落
        when (mParagraph.getInfo().type) {
            TextParagraphType.TEXT_PARAGRAPH -> {
                processTextParagraph(mParagraph)
            }
            TextParagraphType.EMPTY_LINE_PARAGRAPH -> {
                // 空行段落
            }
            TextParagraphType.ENCRYPTED_SECTION_PARAGRAPH -> {
                // 不处理
            }
        }
    }

    private fun processTextParagraph(paragraph: TextParagraph) {
        val entryIterator = paragraph.getIterator()
        var entry: TextParagraphEntry? = null
        while (entryIterator.hasNext()) {
            entry = entryIterator.next()
            when (entry) {
                is TextEntry -> {
                    processTextEntry(entry)
                }
            }
        }
    }

    private fun processTextEntry(textEntry: TextEntry) {
        // 创建缓冲区
        val breakBuffer = ByteArray(1024)
        LineBreaker.setLineBreak(
            textEntry.textData, 0,
            textEntry.textData.size, mTextModel.getLanguage(), breakBuffer
        )

        var spaceState: Int = NO_SPACE
        var wordStart: Int = 0
        var previousChar: Char = 0.toChar()

        textEntry.textData.forEachIndexed { index, ch ->
            if (Character.isWhitespace(ch)) {
                if (index > 0 && spaceState == NO_SPACE) {
                    addWord()
                }
                spaceState = SPACE
            } else if (Character.isSpaceChar(ch)) {// isWhitespace 和 isSpaceChar 表示两种不同的空格
                // 如果之前不存在 SPACE 字符，则添加
                if (index > 0 && spaceState == NO_SPACE) {
                    addWord()
                }

                // 添加一个 NBSpace Element 到局部 elements 中 ==> 禁止换行
                mElementList.add(TextElement.NBSpace)

                // 设置当前状态 spaceState
                if (spaceState != SPACE) {
                    spaceState = NON_BREAKABLE_SPACE
                }
            } else {
                // 如果当前是 Space 状态
                when (spaceState) {
                    SPACE -> {
                        mElementList.add(TextElement.HSpace)
                        // 将 index 设置为 word 的起始位置
                        wordStart = index
                    }

                    NON_BREAKABLE_SPACE -> wordStart = index
                    // 添加非 Break 字符
                    NO_SPACE -> if (index > 0 &&
                        breakBuffer[index - 1] != LineBreaker.NOBREAK &&
                        previousChar != '-' && index != wordStart
                    ) {
                        addWord()
                        wordStart = index
                    }
                }
                spaceState = NO_SPACE
            }
            previousChar = ch
        }
    }

    // 添加单词
    private fun addWord() {
        mElementList.add(TextWordElement())
    }

    // 是否光标在起始位置
    fun isStart(): Boolean {
        return mCurParagraphIndex == 0
    }

    // 是否光标在终止位置
    fun isLast(): Boolean {
        return mCurParagraphIndex == (mTextModel.getParagraphCount() - 1)
    }

    // 获取上一段落光标
    fun preCursor(): TextParagraphCursor? {
        return if (isStart()) null else mParagraphCursor[mCurParagraphIndex - 1]
    }

    // 获取下一个段落光标
    fun nextCursor(): TextParagraphCursor? {
        return if (isLast()) null else mParagraphCursor[mCurParagraphIndex + 1]
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