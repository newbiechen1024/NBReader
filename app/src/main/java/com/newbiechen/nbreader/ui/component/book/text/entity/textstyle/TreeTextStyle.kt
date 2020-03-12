package com.newbiechen.nbreader.ui.component.book.text.entity.textstyle

import com.newbiechen.nbreader.ui.component.book.text.entity.TextMetrics

/**
 *  author : newbiechen
 *  date : 2019-10-26 15:57
 *  description :文本样式接口类
 */

abstract class TreeTextStyle(parentStyle: TreeTextStyle?) : TextStyle {
    // abstract fun getFontEntries(): List<FontEntry>
    val parent: TreeTextStyle? = parentStyle

    // TODO：需要进行缓存处理

    fun getLeftIndent(metrics: TextMetrics): Int {
        return getLeftMargin(metrics) + getLeftPadding(metrics)
    }

    fun getRightIndent(metrics: TextMetrics): Int {
        return getRightMargin(metrics) + getRightPadding(metrics)
    }
}