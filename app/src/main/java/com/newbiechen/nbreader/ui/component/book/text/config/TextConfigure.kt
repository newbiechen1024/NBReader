package com.newbiechen.nbreader.ui.component.book.text.config

import android.graphics.Bitmap
import android.graphics.drawable.Drawable

/**
 *  author : newbiechen
 *  date : 2020/3/11 3:01 PM
 *  description :
 *  TODO：感觉除了 margin 之外都可放在 DefaultStyle 中，先把框架搭建起来。
 */

interface TextConfigure {
    /**
     * 文本距离页面左边的间距
     */
    fun getMarginLeft(): Int

    /**
     *  文本距离页面右边的间距
     */
    fun getMarginRight(): Int

    fun getMarginTop(): Int

    fun getMarginBottom(): Int

    // todo:textColor 放这里合理吗
    fun getTextColor(): Int

    /**
     * 获取背景
     */
    fun getBackground(): Drawable?
}