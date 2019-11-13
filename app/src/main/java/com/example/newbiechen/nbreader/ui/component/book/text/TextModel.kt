package com.example.newbiechen.nbreader.ui.component.book.text

/**
 *  author : newbiechen
 *  date : 2019-09-20 16:48
 *  description :
 */

interface TextModel {
    // 获取 id
    fun getId(): String?

    // 获取语言
    fun getLanguage(): String

    // 获取段落总数
    fun getParagraphCount(): Int

    // 获取文本总长度
    fun getTextLength(index: Int): Int

    fun getParagraph(index: Int): TextParagraph?
}