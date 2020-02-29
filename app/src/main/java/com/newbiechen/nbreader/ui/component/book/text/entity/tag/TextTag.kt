package com.newbiechen.nbreader.ui.component.book.text.entity.tag

/**
 *  author : newbiechen
 *  date : 2020-01-12 13:51
 *  description :文本标签
 */

// 文本标签标记，只用于表示对象是 TextTag
interface TextTag {
    companion object {
        val StyleCloseTag = TextStyleCloseTag()
    }
}


/**
 * 文本标签。
 * @see TextTagType.TEXT
 * @param content：文本内容
 */
data class TextContentTag(val content: String) : TextTag {
    override fun toString(): String {
        return "TextContentTag(content='$content')"
    }
}

/**
 * 控制标签。
 * @see TextTagType.CONTROL
 * @param type: control 标签类型。详见
 * @see TextControlType
 * @param isControlStart: 该标签是否是起始标签。类似于 <p></p> <p> 是起始标签， </p> 为封闭标签。
 */
data class TextControlTag(val type: Byte, val isControlStart: Boolean) : TextTag {
    override fun toString(): String {
        return "TextControlTag(type=$type, isControlStart=$isControlStart)"
    }
}

/**
 * 段落标签。
 * @see TextTagType.PARAGRAPH
 * @param type:段落类型
 * @see
 */
data class TextParagraphTag(val type: Byte) : TextTag {
    override fun toString(): String {
        return "TextParagraphTag(type=$type)"
    }
}

/**
 * 文本数值间距调整标签
 */
data class TextFixedHSpaceTag(val length: Int) : TextTag {
    override fun toString(): String {
        return "TextFixedHSpace(length=$length)"
    }
}

/**
 * 图片临时标签
 */
data class TextImageTag(val id: Int) : TextTag

/**
 * 超链接临时标签
 */
class TextHyperlinkControlTag() : TextTag

/**
 * 样式闭合标签
 */
class TextStyleCloseTag : TextTag