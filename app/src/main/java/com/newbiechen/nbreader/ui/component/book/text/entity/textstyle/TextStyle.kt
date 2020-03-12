package com.newbiechen.nbreader.ui.component.book.text.entity.textstyle

import com.newbiechen.nbreader.ui.component.book.text.entity.TextMetrics

/**
 *  author : newbiechen
 *  date : 2020/3/11 4:43 PM
 *  description :返回值不为 null 的
 */
interface TextStyle {
    fun getFontSize(metrics: TextMetrics): Int
    fun isBold(): Boolean
    fun isItalic(): Boolean
    fun isUnderline(): Boolean
    fun isStrikeThrough(): Boolean
    fun getLeftMargin(metrics: TextMetrics): Int
    fun getRightMargin(metrics: TextMetrics): Int
    fun getLeftPadding(metrics: TextMetrics): Int
    fun getRightPadding(metrics: TextMetrics): Int
    fun getFirstLineIndent(metrics: TextMetrics): Int
    fun getLineSpacePercent(): Int
    fun getVerticalAlign(metrics: TextMetrics): Int
    fun isVerticallyAligned(): Boolean
    fun getSpaceBefore(metrics: TextMetrics): Int
    fun getSpaceAfter(metrics: TextMetrics): Int
    fun getAlignment(): Byte
    fun allowHyphenations(): Boolean
}