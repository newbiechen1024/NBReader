package com.newbiechen.nbreader.ui.component.book.text.entity.textstyle

import com.newbiechen.nbreader.ui.component.book.text.config.ControlStyleInterceptor
import com.newbiechen.nbreader.ui.component.book.text.entity.TextMetrics

/**
 *  author : newbiechen
 *  date : 2020/3/11 6:40 PM
 *  description :拦截控制器样式
 */

// TODO:TextDecoratedStyle 会缓存数据，是否需要一个判断用于确定是否需要缓存

class ControlInterceptorStyle(
    parent: TreeTextStyle,
    private val textKind: Byte,
    private val interceptor: ControlStyleInterceptor
) : TextDecoratedStyle(parent) {
    override fun getFontSizeInternal(metrics: TextMetrics): Int? {
        return interceptor.getFontSize(textKind)
    }

    override fun getSpaceBeforeInternal(metrics: TextMetrics, fontSize: Int): Int? {
        return interceptor.getSpaceBefore(textKind)
    }

    override fun getSpaceAfterInternal(metrics: TextMetrics, fontSize: Int): Int? {
        return interceptor.getSpaceAfter(textKind)
    }

    override fun isItalicInternal(): Boolean? {
        return interceptor.isItalic(textKind)
    }

    override fun isBoldInternal(): Boolean? {
        return interceptor.isBold(textKind)
    }

    override fun isUnderlineInternal(): Boolean? {
        return interceptor.isUnderline(textKind)
    }

    override fun isStrikeThroughInternal(): Boolean? {
        return interceptor.isStrikeThrough(textKind)
    }

    override fun isVerticallyAlignedInternal(): Boolean? {
        return interceptor.isVerticallyAligned(textKind)
    }

    override fun getAlignmentInternal(): Byte? {
        return interceptor.getAlignment(textKind)
    }

    override fun allowHyphenationsInternal(): Boolean? {
        return interceptor.allowHyphenations(textKind)
    }

    override fun getVerticalAlignInternal(metrics: TextMetrics, fontSize: Int): Int? {
        return interceptor.getVerticalAlign(textKind)
    }

    override fun getLeftMarginInternal(metrics: TextMetrics, fontSize: Int): Int? {
        return interceptor.getLeftMargin(textKind)
    }

    override fun getRightMarginInternal(metrics: TextMetrics, fontSize: Int): Int? {
        return interceptor.getRightMargin(textKind)
    }

    override fun getLeftPaddingInternal(metrics: TextMetrics, fontSize: Int): Int? {
        return interceptor.getLeftPadding(textKind)
    }

    override fun getRightPaddingInternal(metrics: TextMetrics, fontSize: Int): Int? {
        return interceptor.getRightPadding(textKind)
    }

    override fun getFirstLineIndentInternal(metrics: TextMetrics, fontSize: Int): Int? {
        return interceptor.getFirstLineIndent(textKind)
    }

    override fun getLineSpacePercentInternal(): Int? {
        return interceptor.getLineSpacePercent(textKind)
    }
}