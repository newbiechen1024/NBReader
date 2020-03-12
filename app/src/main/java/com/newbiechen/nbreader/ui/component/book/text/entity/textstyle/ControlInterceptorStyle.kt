package com.newbiechen.nbreader.ui.component.book.text.entity.textstyle

import com.newbiechen.nbreader.ui.component.book.text.config.ControlStyleInterceptor
import com.newbiechen.nbreader.ui.component.book.text.entity.TextMetrics

/**
 *  author : newbiechen
 *  date : 2020/3/11 6:40 PM
 *  description :拦截控制器样式
 */

class ControlInterceptorStyle(
    private val textKind: Byte,
    private val interceptor: ControlStyleInterceptor
) : NullableTextStyle {

    override fun getFontSize(metrics: TextMetrics): Int? {
        return interceptor.getFontSize(textKind, metrics)
    }

    override fun isBold(): Boolean? {
        return interceptor.isBold(textKind)
    }

    override fun isItalic(): Boolean? {
        return interceptor.isItalic(textKind)
    }

    override fun isUnderline(): Boolean? {
        return interceptor.isUnderline(textKind)
    }

    override fun isStrikeThrough(): Boolean? {
        return interceptor.isStrikeThrough(textKind)
    }

    override fun getLeftMargin(metrics: TextMetrics): Int? {
        return interceptor.getLeftMargin(textKind, metrics)
    }

    override fun getRightMargin(metrics: TextMetrics): Int? {
        return interceptor.getRightMargin(textKind, metrics)
    }

    override fun getLeftPadding(metrics: TextMetrics): Int? {
        return interceptor.getLeftPadding(textKind, metrics)
    }

    override fun getRightPadding(metrics: TextMetrics): Int? {
        return interceptor.getRightPadding(textKind, metrics)
    }

    override fun getFirstLineIndent(metrics: TextMetrics): Int? {
        return interceptor.getFirstLineIndent(textKind, metrics)
    }

    override fun getLineSpacePercent(): Int? {
        return interceptor.getLineSpacePercent(textKind)
    }

    override fun getVerticalAlign(metrics: TextMetrics): Int? {
        return interceptor.getVerticalAlign(textKind, metrics)
    }

    override fun isVerticallyAligned(): Boolean? {
        return interceptor.isVerticallyAligned(textKind)
    }

    override fun getSpaceBefore(metrics: TextMetrics): Int? {
        return interceptor.getSpaceBefore(textKind, metrics)
    }

    override fun getSpaceAfter(metrics: TextMetrics): Int? {
        return interceptor.getSpaceAfter(textKind, metrics)
    }

    override fun getAlignment(): Byte? {
        return interceptor.getAlignment(textKind)
    }

    override fun allowHyphenations(): Boolean? {
        return interceptor.allowHyphenations(textKind)
    }
}