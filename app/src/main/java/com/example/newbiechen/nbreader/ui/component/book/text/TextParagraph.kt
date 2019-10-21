package com.example.newbiechen.nbreader.ui.component.book.text

import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextParagraphEntry
import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextParagraphInfo

/**
 *  author : newbiechen
 *  date : 2019-10-21 15:32
 *  description :
 */

interface TextParagraph {
    // 获取段落详情
    fun getInfo(): TextParagraphInfo

    // 获取 Paragraph entry 遍历器
    fun getEntryIterator(): EntryIterator

    // TextParagraph 可以认为是由一组 entry 组成的，所以需要有一个 entry 遍历器解析 Parapgrah
    interface EntryIterator {
        // 重置参数
        fun reset()

        // 是否存在下一个 Entry
        fun hasNext(): Boolean

        // 获取下一个 Entry
        fun next(): TextParagraphEntry?
    }
}