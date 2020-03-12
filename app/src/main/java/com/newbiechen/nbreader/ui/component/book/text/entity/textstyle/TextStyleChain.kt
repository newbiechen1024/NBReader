package com.newbiechen.nbreader.ui.component.book.text.entity.textstyle

import com.newbiechen.nbreader.ui.component.book.text.entity.TextMetrics

/**
 *  author : newbiechen
 *  date : 2020/3/11 6:55 PM
 *  description :文本样式责任链
 */

class TextStyleChain(
    private val textStyle: NullableTextStyle,
    private val parent: NullableTextStyle?
) : NullableTextStyle {

    override fun getFontSize(metrics: TextMetrics): Int? {
        return textStyle.getFontSize(metrics) ?: parent?.getFontSize(metrics)
    }

    override fun isBold(): Boolean? {
        return textStyle.isBold() ?: parent?.isBold()
    }

    override fun isItalic(): Boolean? {
        return textStyle.isItalic() ?: parent?.isItalic()
    }

    override fun isUnderline(): Boolean? {
        return textStyle.isUnderline() ?: parent?.isUnderline()
    }

    override fun isStrikeThrough(): Boolean? {
        return textStyle.isStrikeThrough() ?: parent?.isStrikeThrough()
    }

    override fun getLeftMargin(metrics: TextMetrics): Int? {
        return textStyle.getLeftMargin(metrics) ?: parent?.getLeftMargin(metrics)
    }

    override fun getRightMargin(metrics: TextMetrics): Int? {
        return textStyle.getRightMargin(metrics) ?: parent?.getRightMargin(metrics)
    }

    override fun getLeftPadding(metrics: TextMetrics): Int? {
        return textStyle.getLeftPadding(metrics) ?: parent?.getLeftPadding(metrics)
    }

    override fun getRightPadding(metrics: TextMetrics): Int? {
        return textStyle.getRightPadding(metrics) ?: parent?.getRightPadding(metrics)
    }

    override fun getFirstLineIndent(metrics: TextMetrics): Int? {
        return textStyle.getFirstLineIndent(metrics) ?: parent?.getFirstLineIndent(metrics)
    }

    override fun getLineSpacePercent(): Int? {
        return textStyle.getLineSpacePercent() ?: parent?.getLineSpacePercent()
    }

    override fun getVerticalAlign(metrics: TextMetrics): Int? {
        return textStyle.getVerticalAlign(metrics) ?: parent?.getVerticalAlign(metrics)
    }

    override fun isVerticallyAligned(): Boolean? {
        return textStyle.isVerticallyAligned() ?: parent?.isVerticallyAligned()
    }

    override fun getSpaceBefore(metrics: TextMetrics): Int? {
        return textStyle.getSpaceBefore(metrics) ?: parent?.getSpaceBefore(metrics)
    }

    override fun getSpaceAfter(metrics: TextMetrics): Int? {
        return textStyle.getSpaceAfter(metrics) ?: parent?.getSpaceAfter(metrics)
    }

    override fun getAlignment(): Byte? {
        return textStyle.getAlignment() ?: parent?.getAlignment()
    }

    override fun allowHyphenations(): Boolean? {
        return textStyle.allowHyphenations() ?: parent?.allowHyphenations()
    }
}