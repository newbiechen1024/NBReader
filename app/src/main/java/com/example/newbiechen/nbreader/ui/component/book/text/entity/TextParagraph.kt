package com.example.newbiechen.nbreader.ui.component.book.text.entity

/**
 *  author : newbiechen
 *  date : 2020-01-12 16:39
 *  description :文本段落
 */

/**
 * @param type：段落类型，具体类型详见
 * @see com.example.newbiechen.nbreader.ui.component.book.text.entity.tag.TextParagraphType
 * @param indexFromChapter：段落在章节中的位置
 * @param startOffset：段落在章节中的起始偏移
 * @param endOffset:段落在章节中的终止偏移
 */
data class TextParagraph(
    val type: Byte,
    val indexFromChapter: Int,
    val startOffset: Int,
    val endOffset: Int
)