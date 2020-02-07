package com.newbiechen.nbreader.ui.component.book.text.entity.tag

/**
 *  author : newbiechen
 *  date : 2020-01-12 13:52
 *  description :文本标签具有的类型
 */

/**
 * 文本标签类型
 *
 */
object TextTagType {
    const val TEXT: Byte = 1 // 文本标签
    const val IMAGE: Byte = 2 // 图片标签
    const val CONTROL: Byte = 3 // 控制标签 (交由上层处理的控制标签，由自己操控)
    const val HYPERLINK_CONTROL: Byte = 4 // 超链接控制标签
    const val STYLE_CSS: Byte = 5
    const val STYLE_OTHER: Byte = 6
    const val STYLE_CLOSE: Byte = 7
    const val FIXED_HSPACE: Byte = 8
    const val RESET_BIDI: Byte = 9
    const val AUDIO: Byte = 10
    const val VIDEO: Byte = 11     // 视频标签
    const val EXTENSION: Byte = 12 // 扩展标签
    const val PARAGRAPH: Byte = 13 // 段落标签
}

/**
 * TextControlTag 标签具有的子类型
 */
object TextControlType {
    const val REGULAR: Byte = 0
}

/**
 * TextParagraphTag 标签具有的子类型
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