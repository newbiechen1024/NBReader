package com.example.newbiechen.nbreader.ui.component.book.text.entity

/**
 *  author : newbiechen
 *  date : 2019-10-20 15:40
 *  description :文本段落数据信息
 */

data class TextParagraphInfo(
    val type: Byte, // 段落类型
    val bufferBlockIndex: Int, // 当前段落所在的缓冲块
    val bufferBlockOffset: Int, // 当前段落在缓冲块的具体位置
    val entryCount: Int, // 当前段落包含标签数。
    val textLength: Int, // 当前段落的长度
    val endLengthFromTotal: Int // 当前段落在总长度中的位置
    )