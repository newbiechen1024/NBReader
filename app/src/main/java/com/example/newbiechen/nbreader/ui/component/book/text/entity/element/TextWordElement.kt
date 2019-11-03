package com.example.newbiechen.nbreader.ui.component.book.text.entity.element

import com.example.newbiechen.nbreader.ui.component.book.text.processor.TextPaintContext

/**
 *  author : newbiechen
 *  date : 2019-10-21 17:36
 *  description :单词元素
 */

class TextWordElement(data: CharArray, offset: Int, length: Int, paragraphOffset: Int) : TextElement() {
    // 字符数组
    val data: CharArray = data
    // 起始位置
    val offset: Int = offset
    // 单词长度
    val length: Int = length
    // 段落偏移
    val paragraphOffset: Int = paragraphOffset

    private var mCacheWidth: Int? = null

    constructor(word: String, paragraphOffset: Int) : this(word.toCharArray(), 0, word.length, paragraphOffset)

    // 检测当前 Word 是否是 WhiteSpace
    fun isASpace(): Boolean {
        for (i in offset until offset + length) {
            if (!Character.isWhitespace(data[i])) {
                return false
            }
        }
        return true
    }

    fun getWidth(paintContext: TextPaintContext): Int {
        if (mCacheWidth == null) {
            mCacheWidth = paintContext.getStringWidth(data, offset, length)
        }
        return mCacheWidth!!
    }

    override fun toString(): String {
        return getString()
    }

    fun getString(): String {
        return String(data, offset, length)
    }
}