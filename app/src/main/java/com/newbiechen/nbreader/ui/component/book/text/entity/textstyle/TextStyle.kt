package com.newbiechen.nbreader.ui.component.book.text.entity.textstyle

import com.newbiechen.nbreader.ui.component.book.text.entity.TextMetrics

/**
 *  author : newbiechen
 *  date : 2020/3/11 4:43 PM
 *  description :返回值不为 null 的
 */
interface TextStyle : NullableTextStyle{
    override fun getFontSize(metrics: TextMetrics): Int
    override fun isBold(): Boolean
    override fun isItalic(): Boolean
    override fun isUnderline(): Boolean
    override fun isStrikeThrough(): Boolean
    override fun getLeftMargin(metrics: TextMetrics): Int
    override fun getRightMargin(metrics: TextMetrics): Int
    override fun getLeftPadding(metrics: TextMetrics): Int
    override fun getRightPadding(metrics: TextMetrics): Int
    override fun getFirstLineIndent(metrics: TextMetrics): Int
    override fun getLineSpacePercent(): Int
    override fun getVerticalAlign(metrics: TextMetrics): Int
    override fun isVerticallyAligned(): Boolean
    override fun getSpaceBefore(metrics: TextMetrics): Int
    override fun getSpaceAfter(metrics: TextMetrics): Int
    override fun getAlignment(): Byte
    override fun allowHyphenations(): Boolean
}