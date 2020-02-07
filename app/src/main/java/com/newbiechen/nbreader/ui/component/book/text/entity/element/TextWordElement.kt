package com.newbiechen.nbreader.ui.component.book.text.entity.element

import com.newbiechen.nbreader.ui.component.book.text.processor.TextPaintContext

/**
 *  author : newbiechen
 *  date : 2019-10-21 17:36
 *  description :单词元素
 */

/**
 * @param data：数据块
 * @param offset:单词在数据块中的偏移
 * @param length:单词的长度
 */
class TextWordElement(data: CharArray, offset: Int, length: Int) : TextElement() {
    // 字节数组
    val data: CharArray = data
    // 起始位置
    val offset: Int = offset
    // 字节长度
    val length: Int = length

    private var mCacheWidth: Int? = null

    constructor(word: String) : this(
        word.toCharArray(),
        0,
        word.length
    )

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