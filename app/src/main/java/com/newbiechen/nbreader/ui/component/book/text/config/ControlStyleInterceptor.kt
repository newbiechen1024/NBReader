package com.newbiechen.nbreader.ui.component.book.text.config

/**
 *  author : newbiechen
 *  date : 2020/3/11 2:49 PM
 *  description :控制样式拦截器
 *
 *  返回的 int 单位是 pixel
 */

interface ControlStyleInterceptor {
    fun getFontSize(textKind: Byte): Int?

    fun isBold(textKind: Byte): Boolean?

    fun isItalic(textKind: Byte): Boolean?

    fun isUnderline(textKind: Byte): Boolean?

    fun isStrikeThrough(textKind: Byte): Boolean?

    fun getLeftMargin(textKind: Byte): Int?

    fun getRightMargin(textKind: Byte): Int?

    fun getLeftPadding(textKind: Byte): Int?

    fun getRightPadding(textKind: Byte): Int?

    fun getFirstLineIndent(textKind: Byte): Int?

    fun getLineSpacePercent(textKind: Byte): Int?

    fun getVerticalAlign(textKind: Byte): Int?

    fun isVerticallyAligned(textKind: Byte): Boolean?

    fun getSpaceBefore(textKind: Byte): Int?

    fun getSpaceAfter(textKind: Byte): Int?

    fun getAlignment(textKind: Byte): Byte?

    fun allowHyphenations(textKind: Byte): Boolean?
}