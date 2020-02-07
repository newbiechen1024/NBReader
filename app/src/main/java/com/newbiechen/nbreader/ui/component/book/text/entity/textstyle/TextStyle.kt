package com.newbiechen.nbreader.ui.component.book.text.entity.textstyle

import com.newbiechen.nbreader.ui.component.book.text.entity.TextMetrics

/**
 *  author : newbiechen
 *  date : 2019-10-26 15:57
 *  description :文本样式接口类
 */

abstract class TextStyle(parentStyle: TextStyle?) {
    // abstract fun getFontEntries(): List<FontEntry>
    val parent: TextStyle = parentStyle ?: this

    abstract fun getFontSize(metrics: TextMetrics): Int

    abstract fun isBold(): Boolean

    abstract fun isItalic(): Boolean

    abstract fun isUnderline(): Boolean

    abstract fun isStrikeThrough(): Boolean

    fun getLeftIndent(metrics: TextMetrics): Int {
        return getLeftMargin(metrics) + getLeftPadding(metrics)
    }

    fun getRightIndent(metrics: TextMetrics): Int {
        return getRightMargin(metrics) + getRightPadding(metrics)
    }

    abstract fun getLeftMargin(metrics: TextMetrics): Int

    abstract fun getRightMargin(metrics: TextMetrics): Int

    abstract fun getLeftPadding(metrics: TextMetrics): Int

    abstract fun getRightPadding(metrics: TextMetrics): Int

    abstract fun getFirstLineIndent(metrics: TextMetrics): Int

    abstract fun getLineSpacePercent(): Int

    abstract fun getVerticalAlign(metrics: TextMetrics): Int

    abstract fun isVerticallyAligned(): Boolean

    abstract fun getSpaceBefore(metrics: TextMetrics): Int

    abstract fun getSpaceAfter(metrics: TextMetrics): Int

    abstract fun getAlignment(): Byte

    // 是否允许断句
    abstract fun allowHyphenations(): Boolean
}