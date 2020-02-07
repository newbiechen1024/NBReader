package com.newbiechen.nbreader.ui.component.book.text.entity.entry

/**
 *  author : newbiechen
 *  date : 2019-10-21 16:14
 *  description :Entry 具有的 type 类型，对应 native 层。
 */

object TextParagraphEntryType  {
    const val TEXT: Byte = 1
    const val IMAGE: Byte = 2
    const val CONTROL: Byte = 3
    const val HYPERLINK_CONTROL: Byte = 4
    const val STYLE_CSS: Byte = 5
    const val STYLE_OTHER: Byte = 6
    const val STYLE_CLOSE: Byte = 7
    const val FIXED_HSPACE: Byte = 8
    const val RESET_BIDI: Byte = 9
    const val AUDIO: Byte = 10
    const val VIDEO: Byte = 11
    const val EXTENSION: Byte = 12
}