package com.newbiechen.nbreader.ui.component.book.text.entity.textstyle

import com.newbiechen.nbreader.ui.component.book.text.entity.TextMetrics
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextFeature
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextStyleTag

/**
 *  author : newbiechen
 *  date : 2020/3/10 7:58 PM
 *  description :Control Tag Text Style
 *  处理 Control Tag 中每种 TextKind 对应的 TextStyle
 *
 *  TextKind 是 HTML 带有样式标签的整合，如:<h1> ~ <h6>,<h6>,<strong>
 */

class ControlTextStyle(
    parent: TreeTextStyle,
    private val description: ControlStyleDescription
) : TextDecoratedStyle(parent) {

    override fun getFontSizeInternal(metrics: TextMetrics): Int? {
        val length = description.getFontSizeAttr() ?: return null
        return TextStyleTag.compute(
            length, metrics, parent.getFontSize(metrics), TextFeature.LENGTH_FONT_SIZE
        )
    }

    override fun isBoldInternal(): Boolean? {
        return description.isBold()
    }

    override fun isItalicInternal(): Boolean? {
        return description.isItalic()
    }

    override fun isUnderlineInternal(): Boolean? {
        return description.isUnderlined()
    }

    override fun isStrikeThroughInternal(): Boolean? {
        return description.isStrikedThrough()
    }

    override fun getLeftMarginInternal(metrics: TextMetrics, fontSize: Int): Int? {
        val length = description.getLeftMarginAttr() ?: return null
        return parent.getLeftMargin(metrics) + TextStyleTag.compute(
            length, metrics, fontSize, TextFeature.LENGTH_MARGIN_LEFT
        )
    }

    override fun getRightMarginInternal(metrics: TextMetrics, fontSize: Int): Int? {
        val length = description.getRightMarginAttr() ?: return null
        return parent.getRightMargin(metrics) + TextStyleTag.compute(
            length, metrics, fontSize, TextFeature.LENGTH_MARGIN_RIGHT
        )
    }

    override fun getLeftPaddingInternal(metrics: TextMetrics, fontSize: Int): Int? {
        val length = description.getLeftPaddingAttr() ?: return null
        return TextStyleTag.compute(
            length, metrics, fontSize, TextFeature.LENGTH_PADDING_LEFT
        )
    }

    override fun getRightPaddingInternal(metrics: TextMetrics, fontSize: Int): Int? {
        val length = description.getRightPaddingAttr() ?: return null
        return TextStyleTag.compute(
            length, metrics, fontSize, TextFeature.LENGTH_PADDING_RIGHT
        )
    }

    override fun getFirstLineIndentInternal(metrics: TextMetrics, fontSize: Int): Int? {
        val length =
            description.getFirstLineIndentAttr() ?: return null
        return TextStyleTag.compute(
            length, metrics, fontSize, TextFeature.LENGTH_FIRST_LINE_INDENT
        )
    }

    override fun getLineSpacePercentInternal(): Int? {
        val lineHeight = description.getLineHeightAttr()

        if (lineHeight == null || !lineHeight.matches(Regex("[1-9][0-9]*%"))) {
            return null
        }

        return lineHeight.substring(0, lineHeight.length - 1).toInt()
    }

    override fun getVerticalAlignInternal(metrics: TextMetrics, fontSize: Int): Int? {
        val length = description.getVerticalAlignAttr() ?: return null
        return TextStyleTag.compute(
            // TODO: add new length for vertical alignment
            length, metrics, fontSize, TextFeature.LENGTH_FONT_SIZE
        )
    }


    override fun isVerticallyAlignedInternal(): Boolean? {
        return description.hasNonZeroVerticalAlign()
    }

    override fun getSpaceBeforeInternal(metrics: TextMetrics, fontSize: Int): Int? {
        val length = description.getSpaceBeforeAttr() ?: return null
        return TextStyleTag.compute(
            length, metrics, fontSize, TextFeature.LENGTH_SPACE_BEFORE
        )
    }

    override fun getSpaceAfterInternal(metrics: TextMetrics, fontSize: Int): Int? {
        val length = description.getSpaceAfterAttr() ?: return null
        return TextStyleTag.compute(
            length, metrics, fontSize, TextFeature.LENGTH_SPACE_AFTER
        )
    }

    override fun getAlignmentInternal(): Byte? {
        return description.getAlignment()
    }

    override fun allowHyphenationsInternal(): Boolean? {
        return description.allowHyphenations()
    }
}