package com.example.newbiechen.nbreader.ui.component.book.text.entity.entry

/**
 *  author : newbiechen
 *  date : 2019-10-21 15:49
 *  description :段落块
 */

open class TextParagraphEntry

/**
 * 文本 entry 信息
 * @param data:数据块(一般存储的是数据的是一个数据块)
 * @param offset:文本对应数据块的偏移位置
 * @param length:文本的长度
 */
class TextEntry(val data: CharArray, val offset: Int, val length: Int) : TextParagraphEntry()

