package com.example.newbiechen.nbreader.ui.component.book.text.entity

import com.example.newbiechen.nbreader.ui.component.book.text.entity.textstyle.TextStyle
import com.example.newbiechen.nbreader.ui.component.book.text.processor.TextParagraphCursor

/**
 *  author : newbiechen
 *  date : 2019-10-23 13:49
 *  description :文本行信息
 */

data class TextLineInfo(
    val paragraphCursor: TextParagraphCursor,
    // 起始行的索引
    val startElementIndex: Int,
    val startCharIndex: Int,
    var startStyle: TextStyle? = null
) {

    val elementCount: Int = paragraphCursor.getElementCount()

    var width: Int = 0
    var height: Int = 0

    var leftIndent: Int = 0
    var descent: Int = 0
    var vSpaceBefore: Int = 0
    var vSpaceAfter: Int = 0
    var spaceCount: Int = 0

    // 结束行的索引
    var endElementIndex: Int = startElementIndex
    var endCharIndex: Int = startCharIndex
    // 去除修改 Style 的 Element 后的 index
    var realStartElementIndex: Int = startElementIndex
    var realStartCharIndex: Int = startCharIndex
    var previousInfoUsed: Boolean = false
    // 是否显示
    var isVisible = false

    /**
     * 是否是段落的最后一行
     */
    fun isEndOfParagraph(): Boolean {
        return endElementIndex == (elementCount - 1)
    }

    /**
     *
     */
    fun adjust(previous: TextLineInfo?) {
        if (!previousInfoUsed && previous != null) {
            height -= Math.min(previous!!.vSpaceAfter, vSpaceBefore)
            previousInfoUsed = true
        }
    }

}