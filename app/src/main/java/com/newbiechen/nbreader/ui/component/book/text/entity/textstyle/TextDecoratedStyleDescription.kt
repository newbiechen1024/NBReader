package com.newbiechen.nbreader.ui.component.book.text.entity.textstyle

import com.newbiechen.nbreader.ui.component.book.text.entity.TextMetrics
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextFeature
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextSizeUnit
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextStyleTag
import java.util.HashMap

/**
 *  author : newbiechen
 *  date : 2019-10-26 17:41
 *  description :自定义的文本装饰样式描述，用于处理在 Book 中添加的自定义的装饰标签时，对应的默认样式。
 *
 *  如：解析在书籍的某一段自己加上了类似 <p>xxx</p> 的标签。那么就需要有对应该标签的样式。
 */

/**
 * 结构参考 css:
 * p {
 *    fbreader-id: 0;
 *    fbreader-name: "Regular Paragraph";
 *    text-indent: 10pt;
 *    hyphens: auto;
 * }
 *
 * @param selectorName: 选择器的名字，如 p
 * @param valueMap:选择器包含的值
 */
class TextDecoratedStyleDescription(
    private val selectorName: String,
    private val valueMap: Map<String, String>
) {

    companion object {
        // 自定义 css 具有的属性名

        const val ATTR_FONT_FAMILY = "font-family"
        const val ATTR_FONT_SIZE = "font-size"
        const val ATTR_FONT_WEIGHT = "font-weight"
        const val ATTR_FONT_STYLE = "font-style"
        const val ATTR_TEXT_DESCORATION = "text-decoration"
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

    fun getFontFamily(): String? {
        return valueMap[ATTR_FONT_FAMILY]
    }

    fun getFontSize(metrics: TextMetrics, defaultFontSize: Int): Int {
        val length = parseLength(valueMap[ATTR_FONT_SIZE]) ?: return defaultFontSize
        return TextStyleTag.compute(
            length, metrics, defaultFontSize, TextFeature.LENGTH_FONT_SIZE
        )
    }

    fun getVerticalAlign(metrics: TextMetrics, base: Int, fontSize: Int): Int {
        val length = parseLength(valueMap[ATTR_VERTICAL_ALIGN]) ?: return base
        return TextStyleTag.compute(
            // TODO: add new length for vertical alignment
            length, metrics, fontSize, TextFeature.LENGTH_FONT_SIZE
        )
    }

    fun hasNonZeroVerticalAlign(): Boolean {
        val length = parseLength(valueMap[ATTR_VERTICAL_ALIGN])
        return length != null && length.size != 0.toShort()
    }

    fun getLeftMargin(metrics: TextMetrics, base: Int, fontSize: Int): Int {
        val length = parseLength(valueMap[ATTR_MARGIN_LEFT]) ?: return base
        return base + TextStyleTag.compute(
            length, metrics, fontSize, TextFeature.LENGTH_MARGIN_LEFT
        )
    }

    fun getRightMargin(metrics: TextMetrics, base: Int, fontSize: Int): Int {
        val length = parseLength(valueMap[ATTR_MARGIN_RIGHT]) ?: return base
        return base + TextStyleTag.compute(
            length, metrics, fontSize, TextFeature.LENGTH_MARGIN_RIGHT
        )
    }

    fun getLeftPadding(metrics: TextMetrics, base: Int, fontSize: Int): Int {
        return base
    }

    fun getRightPadding(metrics: TextMetrics, base: Int, fontSize: Int): Int {
        return base
    }

    fun getFirstLineIndent(metrics: TextMetrics, base: Int, fontSize: Int): Int {
        val length = parseLength(valueMap[ATTR_TEXT_INDENT]) ?: return base
        return TextStyleTag.compute(
            length, metrics, fontSize, TextFeature.LENGTH_FIRST_LINE_INDENT
        )
    }

    fun getSpaceBefore(metrics: TextMetrics, base: Int, fontSize: Int): Int {
        val length = parseLength(valueMap[ATTR_MARGIN_TOP]) ?: return base
        return TextStyleTag.compute(
            length, metrics, fontSize, TextFeature.LENGTH_SPACE_BEFORE
        )
    }

    fun getSpaceAfter(metrics: TextMetrics, base: Int, fontSize: Int): Int {
        val length = parseLength(valueMap[ATTR_MARGIN_BOTTOM]) ?: return base
        return TextStyleTag.compute(
            length, metrics, fontSize, TextFeature.LENGTH_SPACE_AFTER
        )
    }

    fun getLineHeight(): String? {
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
        return when (valueMap[ATTR_TEXT_DESCORATION]) {
            "underline" -> true
            "inherit", "" -> null
            else -> false
        }
    }

    fun isStrikedThrough(): Boolean? {
        return when (valueMap[ATTR_TEXT_DESCORATION]) {
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