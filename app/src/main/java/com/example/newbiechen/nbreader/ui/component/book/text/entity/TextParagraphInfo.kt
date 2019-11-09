package com.example.newbiechen.nbreader.ui.component.book.text.entity

/**
 *  author : newbiechen
 *  date : 2019-10-20 15:40
 *  description :文本段落数据信息
 */

/**
 * @param type: 段落类型，参考
 * @see TextParagraphType
 *
 * @param bufferBlockIndex:当前段落所在的缓冲块
 * @param bufferBlockOffset:当前段落在缓冲块的具体位置
 * @param entryCount:当前段落包含标签数
 * @param textLength:当前段落的长度
 * @param endLengthFromTotal:当前段落在总长度中的位置
 */
data class TextParagraphInfo(
    val type: Byte,
    val bufferBlockIndex: Int,
    val bufferBlockOffset: Int,
    val entryCount: Int,
    val textLength: Int,
    val endLengthFromTotal: Int
)