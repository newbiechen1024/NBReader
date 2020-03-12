package com.newbiechen.nbreader.ui.component.book.text.entity.textstyle

import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextSizeUnit
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextStyleTag

/**
 *  author : newbiechen
 *  date : 2020/3/12 12:03 AM
 *  description :样式信息描述类
 */
// TODO:暂时这么设置
class ControlStyleDescription(
    private val valueMap: Map<String, String>
) {
    companion object {
        // 自定义 css 具有的属性名
        const val ATTR_FONT_FAMILY = "font-family"
        const val ATTR_FONT_SIZE = "font-size"
        const val ATTR_FONT_WEIGHT = "font-weight"
        const val ATTR_FONT_STYLE = "font-style"
        const val ATTR_TEXT_DECORATION = "text-decoration"
        const val ATTR_HYPHENS = "hyphens"
        const val ATTR_MARGIN_TOP = "margin-top"
        const val ATTR_MARGIN_BOTTOM = "margin-bottom"
        const val ATTR_MARGIN_LEFT = "margin-left"
        const val ATTR_MARGIN_RIGHT = "margin-right"
        const val ATTR_TEXT_INDENT = "text-indent"
        const val ATTR_TEXT_ALIGN = "text-align"
        const val ATTR_VERTICAL_ALIGN = "vertical-align"
        const val ATTR_LINE_HEIGHT = "line-height"
    }

    fun getFontFamilyAttr(): String? {
        return valueMap[ATTR_FONT_FAMILY]
    }

    fun getFontSizeAttr(): TextStyleTag.Length? {
        return parseLength(valueMap[ATTR_FONT_SIZE])
    }

    fun getVerticalAlignAttr(): TextStyleTag.Length? {
        return parseLength(valueMap[ATTR_VERTICAL_ALIGN])
    }

    fun hasNonZeroVerticalAlign(): Boolean {
        val length = parseLength(valueMap[ATTR_VERTICAL_ALIGN])
        return length != null && length.size != 0.toShort()
    }

    fun getLeftMarginAttr(): TextStyleTag.Length? {
        return parseLength(valueMap[ATTR_MARGIN_LEFT])
    }

    fun getRightMarginAttr(): TextStyleTag.Length? {
        return parseLength(valueMap[ATTR_MARGIN_RIGHT])
    }

    fun getLeftPaddingAttr(): TextStyleTag.Length? {
        return null
    }

    fun getRightPaddingAttr(): TextStyleTag.Length? {
        return null
    }

    fun getFirstLineIndentAttr(): TextStyleTag.Length? {
        return parseLength(valueMap[ATTR_TEXT_INDENT])
    }

    fun getSpaceBeforeAttr(): TextStyleTag.Length? {
        return parseLength(valueMap[ATTR_MARGIN_TOP])
    }

    fun getSpaceAfterAttr(): TextStyleTag.Length? {
        return parseLength(valueMap[ATTR_MARGIN_BOTTOM])
    }

    fun getLineHeightAttr(): String? {
        return valueMap[ATTR_LINE_HEIGHT]
    }

    fun isBold(): Boolean? {
        return when (valueMap[ATTR_FONT_WEIGHT]) {
            "bold" -> true
            "normal" -> false
            else -> null
        }
    }

    fun isItalic(): Boolean? {
        return when (valueMap[ATTR_FONT_STYLE]) {
            "italic", "oblique" -> true
            "normal" -> false
            else -> null
        }
    }

    fun isUnderlined(): Boolean? {
        return when (valueMap[ATTR_TEXT_DECORATION]) {
            "underline" -> true
            "inherit", "" -> null
            else -> false
        }
    }

    fun isStrikedThrough(): Boolean? {
        return when (valueMap[ATTR_TEXT_DECORATION]) {
            "line-through" -> true
            "inherit", "" -> null
            else -> false
        }
    }

    fun getAlignment(): Byte {
        val alignment = valueMap[ATTR_TEXT_ALIGN]
        return if (alignment == null || alignment.isEmpty()) {
            TextAlignmentType.ALIGN_UNDEFINED
        } else if ("center" == alignment) {
            TextAlignmentType.ALIGN_CENTER
        } else if ("left" == alignment) {
            TextAlignmentType.ALIGN_LEFT
        } else if ("right" == alignment) {
            TextAlignmentType.ALIGN_RIGHT
        } else if ("justify" == alignment) {
            TextAlignmentType.ALIGN_JUSTIFY
        } else {
            TextAlignmentType.ALIGN_UNDEFINED
        }
    }

    fun allowHyphenations(): Boolean? {
        return when (valueMap[ATTR_HYPHENS]) {
            "auto" -> true
            "none" -> false
            else -> null
        }
    }

    private val ourCache = HashMap<String, Any>()
    private val ourNullObject = Any()

    private fun parseLength(value: String?): TextStyleTag.Length? {
        if (value == null || value.isEmpty()) {
            return null
        }

        val cached = ourCache[value]
        if (cached != null) {
            return if (cached === ourNullObject) null else cached as TextStyleTag.Length?
        }

        var length: TextStyleTag.Length? = null
        try {
            when {
                value.endsWith("%") -> length = TextStyleTag.Length(
                    java.lang.Short.valueOf(value.substring(0, value.length - 1)),
                    TextSizeUnit.PERCENT
                )
                value.endsWith("rem") -> length = TextStyleTag.Length(
                    (100 * java.lang.Double.valueOf(
                        value.substring(
                            0,
                            value.length - 2
                        )
                    )).toShort(),
                    TextSizeUnit.REM_100
                )
                value.endsWith("em") -> length = TextStyleTag.Length(
                    (100 * java.lang.Double.valueOf(
                        value.substring(
                            0,
                            value.length - 2
                        )
                    )).toShort(),
                    TextSizeUnit.EM_100
                )
                value.endsWith("ex") -> length = TextStyleTag.Length(
                    (100 * java.lang.Double.valueOf(
                        value.substring(
                            0,
                            value.length - 2
                        )
                    )).toShort(),
                    TextSizeUnit.EX_100
                )
                value.endsWith("px") -> length = TextStyleTag.Length(
                    java.lang.Short.valueOf(value.substring(0, value.length - 2)),
                    TextSizeUnit.PIXEL
                )
                value.endsWith("pt") -> length = TextStyleTag.Length(
                    java.lang.Short.valueOf(value.substring(0, value.length - 2)),
                    TextSizeUnit.POINT
                )
            }
        } catch (e: Exception) {
            // ignore
        }

        ourCache[value] = length ?: ourNullObject
        return length
    }
}