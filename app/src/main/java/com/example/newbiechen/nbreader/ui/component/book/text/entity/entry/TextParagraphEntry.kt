package com.example.newbiechen.nbreader.ui.component.book.text.entity.entry

/**
 *  author : newbiechen
 *  date : 2019-10-21 15:49
 *  description :段落块
 */

open class TextParagraphEntry(val type: Byte)

class TextEntry(val textData: CharArray) : TextParagraphEntry(
    TextParagraphEntryType.TEXT
)

