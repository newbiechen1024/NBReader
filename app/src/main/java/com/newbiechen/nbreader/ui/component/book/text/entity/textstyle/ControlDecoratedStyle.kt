package com.newbiechen.nbreader.ui.component.book.text.entity.textstyle

import com.newbiechen.nbreader.ui.component.book.text.entity.TextMetrics

/**
 *  author : newbiechen
 *  date : 2020/3/11 3:10 PM
 *  description :control style 的装饰 style
 */
class ControlDecoratedStyle(
    // 父对象
    parent: TreeTextStyle,
    private val controlStyle: TreeTextStyle
) : TextDecoratedStyle(parent) {

    override fun getFontSizeInternal(metrics: TextMetrics): Int {
        return controlStyle.getFontSize(metrics)
    }

    override fun isBoldInternal(): Boolean? {
        return controlStyle.isBold()
    }

    override fun isItalicInternal(): Boolean? {
        return controlStyle.isItalic()
    }

    override fun isUnderlineInternal(): Boolean? {
        return controlStyle.isUnderline()
    }

    override fun isStrikeThroughInternal(): Boolean? {
        return controlStyle.isStrikeThrough()
    }

    override fun getLeftMarginInternal(metrics: TextMetrics, fontSize: Int): Int? {
        return controlStyle.getLeftMargin(metrics)
    }

    override fun getRightMarginInternal(metrics: TextMetrics, fontSize: Int): Int? {
        return controlStyle.getRightMargin(metrics)
    }

    override fun getLeftPaddingInternal(metrics: TextMetrics, fontSize: Int): Int? {
        return controlStyle.getLeftPadding(metrics)
    }

    override fun getRightPaddingInternal(metrics: TextMetrics, fontSize: Int): Int? {
        return controlStyle.getRightPadding(metrics)
    }

    override fun getFirstLineIndentInternal(metrics: TextMetrics, fontSize: Int): Int? {
        return controlStyle.getFirstLineIndent(metrics)
    }

    override fun getLineSpacePercentInternal(): Int? {
        return controlStyle.getLineSpacePercent()
    }

    override fun getVerticalAlignInternal(metrics: TextMetrics, fontSize: Int): Int? {
        return controlStyle.getVerticalAlign(metrics)
    }

    override fun isVerticallyAlignedInternal(): Boolean? {
        return controlStyle.isVerticallyAligned()
    }

    override fun getAlignmentInternal(): Byte? {
        return controlStyle.getAlignment()
    }

    override fun allowHyphenationsInternal(): Boolean? {
        return controlStyle.allowHyphenations()
    }

    override fun getSpaceBeforeInternal(metrics: TextMetrics, fontSize: Int): Int? {
        return controlStyle.getSpaceBefore(metrics)
    }

    override fun getSpaceAfterInternal(metrics: TextMetrics, fontSize: Int): Int? {
        return controlStyle.getSpaceAfter(metrics)
    }
}