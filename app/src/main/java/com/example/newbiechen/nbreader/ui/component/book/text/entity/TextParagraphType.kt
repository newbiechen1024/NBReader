package com.example.newbiechen.nbreader.ui.component.book.text.entity

/**
 *  author : newbiechen
 *  date : 2019-10-21 17:24
 *  description :
 */

object TextParagraphType {
    const val TEXT_PARAGRAPH: Byte = 0 // 文本行
    //byte TREE_PARAGRAPH = 1;
    const val EMPTY_LINE_PARAGRAPH: Byte = 2 // 空行
    const val BEFORE_SKIP_PARAGRAPH: Byte = 3
    const val AFTER_SKIP_PARAGRAPH: Byte = 4
    const val END_OF_SECTION_PARAGRAPH: Byte = 5 // 结束段落
    const val PSEUDO_END_OF_SECTION_PARAGRAPH: Byte = 6 // 伪结束段落 ==> 这是什么鬼东西
    const val END_OF_TEXT_PARAGRAPH: Byte = 7
    const val ENCRYPTED_SECTION_PARAGRAPH: Byte = 8 // 加密段落
}