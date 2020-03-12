package com.newbiechen.nbreader.ui.component.book.text.entity.textstyle

import com.newbiechen.nbreader.ui.component.book.text.entity.TextMetrics

/**
 *  author : newbiechen
 *  date : 2020/3/11 11:52 PM
 *  description :基础样式类
 */

abstract class BaseTextStyle : TreeTextStyle(null) {
    abstract fun getFontSize(): Int
    override fun getFontSize(metrics: TextMetrics): Int {
        return getFontSize()
    }
}