package com.newbiechen.nbreader.ui.component.book.text.config

import com.newbiechen.nbreader.ui.component.book.text.entity.TextMetrics

/**
 *  author : newbiechen
 *  date : 2020/3/11 2:49 PM
 *  description :控制样式拦截器
 */

interface ControlStyleInterceptor {
    fun getFontSize(textKind: Byte, metrics: TextMetrics): Int?

    fun isBold(textKind: Byte): Boolean?

    fun isItalic(textKind: Byte): Boolean?

    fun isUnderline(textKind: Byte): Boolean?

    fun isStrikeThrough(textKind: Byte): Boolean?

    fun getLeftMargin(textKind: Byte, metrics: TextMetrics): Int?

    fun getRightMargin(textKind: Byte, metrics: TextMetrics): Int?

    fun getLeftPadding(textKind: Byte, metrics: TextMetrics): Int?

    fun getRightPadding(textKind: Byte, metrics: TextMetrics): Int?

    fun getFirstLineIndent(textKind: Byte, metrics: TextMetrics): Int?

    fun getLineSpacePercent(textKind: Byte): Int?

    fun getVerticalAlign(textKind: Byte, metrics: TextMetrics): Int?

    fun isVerticallyAligned(textKind: Byte): Boolean?

    fun getSpaceBefore(textKind: Byte, metrics: TextMetrics): Int?

    fun getSpaceAfter(textKind: Byte, metrics: TextMetrics): Int?

    fun getAlignment(textKind: Byte): Byte?

    fun allowHyphenations(textKind: Byte): Boolean?
}