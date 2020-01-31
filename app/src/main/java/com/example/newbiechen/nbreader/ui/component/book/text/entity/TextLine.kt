package com.example.newbiechen.nbreader.ui.component.book.text.entity

import com.example.newbiechen.nbreader.ui.component.book.text.entity.textstyle.TextStyle
import com.example.newbiechen.nbreader.ui.component.book.text.processor.cursor.TextParagraphCursor

/**
 *  author : newbiechen
 *  date : 2019-10-23 13:49
 *  description :文本行信息
 */

data class TextLine(
    val paragraphCursor: TextParagraphCursor,
    // 指向当前行元素的起始位置
    val startElementIndex: Int,
    val startCharIndex: Int,
    var startStyle: TextStyle
) {

    val elementCount: Int = paragraphCursor.getElementCount()
    // 当前行的宽高
    var width: Int = 0
    var height: Int = 0
    // 当前行的左缩进
    var leftIndent: Int = 0
    // 文字的基准线
    var descent: Int = 0
    // 当前行的 topMargin
    var vSpaceBefore: Int = 0
    // 当前行的 bottomMargin
    var vSpaceAfter: Int = 0
    // 空格数
    var spaceCount: Int = 0

    // 结束行的索引，允许指向值的末尾。(即，其大小允许 >= elementCount)
    var endElementIndex: Int = startElementIndex
    var endCharIndex: Int = startCharIndex
    // 去除修改 Style 的 Element 后的 index
    var realStartElementIndex: Int = startElementIndex
    var realStartCharIndex: Int = startCharIndex
    var previousInfoUsed: Boolean = false
    // 是否显示
    var isVisible = false

    /**
     * 是否是指向段落元素的末尾
     */
    fun isEndOfParagraph(): Boolean {
        return endElementIndex == elementCount
    }

    /**
     * 跟上一行进行裁决，更新当前 textLine 信息
     */
    fun adjust(previous: TextLine?) {
        if (!previousInfoUsed && previous != null) {
            height -= previous!!.vSpaceAfter.coerceAtMost(vSpaceBefore)
            previousInfoUsed = true
        }
    }

    override fun equals(o: Any?): Boolean {
        val info = o as TextLine?
        return paragraphCursor === info!!.paragraphCursor &&
                startElementIndex == info!!.startElementIndex &&
                startCharIndex == info!!.startCharIndex
    }

    override fun hashCode(): Int {
        return paragraphCursor.hashCode() + startElementIndex + 239 * startCharIndex
    }
}