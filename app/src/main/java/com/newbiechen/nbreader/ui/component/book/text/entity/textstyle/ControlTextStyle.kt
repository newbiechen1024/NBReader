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

    override fun getFontSizeInternal(metrics: TextMetrics): Int {
        val length = description.getFontSizeAttr() ?: return parent!!.getFontSize(metrics)
        return TextStyleTag.compute(
            length, metrics, parent!!.getFontSize(metrics), TextFeature.LENGTH_FONT_SIZE
        )
    }

    override fun isBoldInternal(): Boolean {
        return description.isBold() ?: parent!!.isBold()
    }

    override fun isItalicInternal(): Boolean {
        return description.isItalic() ?: parent!!.isItalic()
    }

    override fun isUnderlineInternal(): Boolean {
        return description.isUnderlined() ?: parent!!.isUnderline()
    }

    override fun isStrikeThroughInternal(): Boolean {
        return description.isStrikedThrough() ?: parent!!.isStrikeThrough()
    }

    override fun getLeftMarginInternal(metrics: TextMetrics, fontSize: Int): Int {
        val length = description.getLeftMarginAttr() ?: return parent!!.getLeftMargin(metrics)
        return parent!!.getLeftMargin(metrics) + TextStyleTag.compute(
            length, metrics, fontSize, TextFeature.LENGTH_MARGIN_LEFT
        )
    }

    override fun getRightMarginInternal(metrics: TextMetrics, fontSize: Int): Int {
        val length = description.getRightMarginAttr() ?: return parent!!.getRightMargin(metrics)
        return parent!!.getRightMargin(metrics) + TextStyleTag.compute(
            length, metrics, fontSize, TextFeature.LENGTH_MARGIN_RIGHT
        )
    }

    override fun getLeftPaddingInternal(metrics: TextMetrics, fontSize: Int): Int {
        val length = description.getLeftPaddingAttr() ?: return parent!!.getLeftPadding(metrics)
        return TextStyleTag.compute(
            length, metrics, fontSize, TextFeature.LENGTH_PADDING_LEFT
        )
    }

    override fun getRightPaddingInternal(metrics: TextMetrics, fontSize: Int): Int {
        val length = description.getRightPaddingAttr() ?: return parent!!.getRightPadding(metrics)
        return TextStyleTag.compute(
            length, metrics, fontSize, TextFeature.LENGTH_PADDING_RIGHT
        )
    }

    override fun getFirstLineIndentInternal(metrics: TextMetrics, fontSize: Int): Int {
        val length =
            description.getFirstLineIndentAttr() ?: return parent!!.getFirstLineIndent(metrics)
        return TextStyleTag.compute(
            length, metrics, fontSize, TextFeature.LENGTH_FIRST_LINE_INDENT
        )
    }

    override fun getLineSpacePercentInternal(): Int {
        val lineHeight = description.getLineHeightAttr()
        return if (lineHeight == null || !lineHeight.matches(Regex("[1-9][0-9]*%"))) {
            parent!!.getLineSpacePercent()
        } else {
            lineHeight.substring(0, lineHeight.length - 1).toInt()
        }
    }

    override fun getVerticalAlignInternal(metrics: TextMetrics, fontSize: Int): Int {
        val length = description.getVerticalAlignAttr() ?: return parent!!.getVerticalAlign(metrics)
        return TextStyleTag.compute(
            // TODO: add new length for vertical alignment
            length, metrics, fontSize, TextFeature.LENGTH_FONT_SIZE
        )
    }


    override fun isVerticallyAlignedInternal(): Boolean {
        return description.hasNonZeroVerticalAlign()
    }

    override fun getSpaceBeforeInternal(metrics: TextMetrics, fontSize: Int): Int {
        val length = description.getSpaceBeforeAttr() ?: return parent!!.getSpaceBefore(metrics)
        return TextStyleTag.compute(
            length, metrics, fontSize, TextFeature.LENGTH_SPACE_BEFORE
        )
    }

    override fun getSpaceAfterInternal(metrics: TextMetrics, fontSize: Int): Int {
        val length = description.getSpaceAfterAttr() ?: return parent!!.getSpaceAfter(metrics)
        return TextStyleTag.compute(
            length, metrics, fontSize, TextFeature.LENGTH_SPACE_AFTER
        )
    }

    override fun getAlignment(): Byte {
        return description.getAlignment()
    }

    override fun allowHyphenations(): Boolean {
        return description.allowHyphenations() ?: parent!!.allowHyphenations()
    }
}