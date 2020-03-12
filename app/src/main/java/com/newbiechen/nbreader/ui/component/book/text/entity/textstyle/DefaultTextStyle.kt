package com.newbiechen.nbreader.ui.component.book.text.entity.textstyle

import com.newbiechen.nbreader.ui.component.book.text.entity.TextMetrics

/**
 *  author : newbiechen
 *  date : 2019-10-26 16:00
 *  description :文本样式默认实现类
 *
 */
class DefaultTextStyle private constructor() : BaseTextStyle() {


    companion object {
        @Volatile
        private var instance: DefaultTextStyle? = null

        fun getInstance() = instance ?: synchronized(this) {
            DefaultTextStyle()
        }
    }

    override fun getFontSize(): Int {
        return 30
    }

    override fun isBold(): Boolean {
        return false
    }

    override fun isItalic(): Boolean {
        return false
    }

    override fun isUnderline(): Boolean {
        return false
    }

    override fun isStrikeThrough(): Boolean {
        return false
    }

    override fun getLeftMargin(metrics: TextMetrics): Int {
        return 0
    }

    override fun getRightMargin(metrics: TextMetrics): Int {
        return 0
    }

    override fun getLeftPadding(metrics: TextMetrics): Int {
        return 0
    }

    override fun getRightPadding(metrics: TextMetrics): Int {
        return 0
    }

    override fun getFirstLineIndent(metrics: TextMetrics): Int {
        return 0
    }

    override fun getLineSpacePercent(): Int {
        return 12 * 10
    }

    override fun getVerticalAlign(metrics: TextMetrics): Int {
        return 0
    }

    override fun isVerticallyAligned(): Boolean {
        return false
    }

    override fun getSpaceBefore(metrics: TextMetrics): Int {
        return 0
    }

    override fun getSpaceAfter(metrics: TextMetrics): Int {
        return 0
    }

    override fun getAlignment(): Byte {
        return TextAlignmentType.ALIGN_JUSTIFY
    }

    override fun allowHyphenations(): Boolean {
        return true
    }
}