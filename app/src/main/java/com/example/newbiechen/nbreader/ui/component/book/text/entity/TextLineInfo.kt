package com.example.newbiechen.nbreader.ui.component.book.text.entity

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
    // 结束行的索引
    var endElementIndex: Int = startElementIndex,
    var endCharIndex: Int = startCharIndex,
    var width: Int = 0,
    var height: Int = 0,

    var leftIndent: Int = 0,
    var descent: Int = 0,
    var vSpaceBefore: Int = 0,
    var vSpaceAfter: Int = 0
)