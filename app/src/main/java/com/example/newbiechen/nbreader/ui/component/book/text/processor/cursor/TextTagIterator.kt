package com.example.newbiechen.nbreader.ui.component.book.text.processor.cursor

import com.example.newbiechen.nbreader.ui.component.book.text.entity.tag.TextTag

/**
 *  author : newbiechen
 *  date : 2020-01-19 11:34
 *  description :文本标签遍历器
 */

interface TextTagIterator {
    fun hasNext(): Boolean
    fun next(): TextTag
    fun reset()
}