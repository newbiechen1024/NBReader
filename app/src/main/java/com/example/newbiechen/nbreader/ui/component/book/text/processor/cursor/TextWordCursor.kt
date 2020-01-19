package com.example.newbiechen.nbreader.ui.component.book.text.processor.cursor

import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextPosition
import com.example.newbiechen.nbreader.ui.component.book.text.entity.element.TextWordElement

/**
 *  author : newbiechen
 *  date : 2019-10-22 14:04
 *  description :文本单词光标，对 TextParagraphCursor 的进一步封装。主要，用于定位在文本中的位置
 */

class TextWordCursor : TextPosition {

    private lateinit var mParagraphCursor: TextParagraphCursor

    // Word 当前指向段落中 element 的位置，支持指向 element 的末尾，即其大小 >= elementCount (需要注意的点)
    private var mElementIndex: Int = 0

    // 指向 word 中字节的位置，支持指向 char 的末尾，即其大小 >= charCount (需要注意的点)
    private var mCharIndex: Int = 0

    constructor(wordCursor: TextWordCursor) {
        updateCursor(wordCursor)
    }

    constructor(paragraphCursor: TextParagraphCursor) {
        updateCursor(paragraphCursor)
    }

    /**
     * 更新当前光标位置
     */
    fun updateCursor(paragraphCursor: TextParagraphCursor) {
        mParagraphCursor = paragraphCursor
        mElementIndex = 0
        mCharIndex = 0
    }

    fun updateCursor(wordCursor: TextWordCursor) {
        mParagraphCursor = wordCursor.getParagraphCursor()
        mElementIndex = wordCursor.getElementIndex()
        mCharIndex = wordCursor.getCharIndex()
    }

    override fun getChapterIndex(): Int {
        return mParagraphCursor.getChapterIndex()
    }


    override fun getParagraphIndex(): Int {
        return mParagraphCursor.getParagraphIndex()
    }

    /**
     * 返回当前光标指向的 element
     */
    override fun getElementIndex() = mElementIndex

    /**
     * 返回当前光标指向的字节
     */
    override fun getCharIndex() = mCharIndex

    /**
     * 返回 Word 光标对应的 Paragraph 光标
     */
    fun getParagraphCursor() = mParagraphCursor

    /**
     * 移动光标到下一个单词
     */
    fun moveToNextWord() {
        // TODO:没有判断 element 的索引超出的问题。

        mElementIndex++
        mCharIndex = 0
    }

    /**
     * 移动光标到上一个单词
     */
    fun moveToPrevWord() {
        // TODO:没有判断 element 的索引超出的问题。

        mElementIndex--
        mCharIndex = 0
    }

    /**
     * 光标移动到下一个段落的开头位置
     * @return 是否成功移动到下一个段落
     */
    fun moveToNextParagraph(): Boolean {
        return if (mParagraphCursor.isLastOfText()) {
            false
        } else {
            mParagraphCursor = mParagraphCursor.nextCursor()!!
            // 跳转到当前段落的起始
            moveToParagraphStart()
            true
        }
    }

    /**
     * 光标移动到上一个段落，的开头位置
     * @return 是否成功移动到上一个段落
     */
    fun moveToPrevParagraph(): Boolean {
        return if (mParagraphCursor.isLastOfText()) {
            false
        } else {
            mParagraphCursor = mParagraphCursor.prevCursor()!!
            // 跳转到下一段落的开头
            moveToParagraphStart()
            true
        }
    }

    /**
     * 移动到段落的指定位置
     */
    fun moveTo(elementIndex: Int, charIndex: Int) {
        if (elementIndex == 0 && charIndex == 0) {
            mElementIndex = 0
            mCharIndex = 0
        } else {
            var elementIndex = 0.coerceAtLeast(elementIndex)
            val elementCount = mParagraphCursor.getElementCount()

            if (elementIndex >= elementCount) {
                mElementIndex = elementCount
                mCharIndex = 0
            } else {
                mElementIndex = elementIndex
                moveToCharIndex(charIndex)
            }
        }
    }

    /**
     * 光标移动到当前 element 中的字节位置
     */
    fun moveToCharIndex(charIndex: Int) {

        // TODO:需要检测越界

        var charIndex = charIndex
        charIndex = 0.coerceAtLeast(charIndex)
        mCharIndex = 0

        if (charIndex > 0) {
            val element = mParagraphCursor.getElement(mElementIndex)
            if (element is TextWordElement) {
                if (charIndex <= element.length) {
                    mCharIndex = charIndex
                }
            }
        }
    }

    /**
     * 光标是否在段落的开头
     */
    fun isStartOfParagraph(): Boolean {
        return mElementIndex == 0 && mCharIndex == 0
    }

    /**
     * 是否是文本的开头
     */
    fun isStartOfText(): Boolean {
        return isStartOfParagraph() && mParagraphCursor.isFirstOfText()
    }

    /**
     * 光标是否指向段落的末尾
     */
    fun isEndOfParagraph(): Boolean {
        return mElementIndex == mParagraphCursor.getElementCount()
    }

    /**
     * 光标是否指向文本的末尾
     */
    fun isEndOfText(): Boolean {
        return isEndOfParagraph() && mParagraphCursor.isLastOfText()
    }

    /**
     * 移动到段落的起始位置
     */
    fun moveToParagraphStart() {
        mElementIndex = 0
        mCharIndex = 0
    }

    /**
     * 跳转到段落的末尾位置
     */
    fun moveToParagraphEnd() {
        mElementIndex = mParagraphCursor.getElementCount()
        mCharIndex = 0
    }
}