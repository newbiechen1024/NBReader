package com.newbiechen.nbreader.ui.component.book.text.config


/**
 *  author : newbiechen
 *  date : 2020/3/11 2:50 PM
 *  description :支持的 CSS 标签类型
 */

interface CSSStyleInterceptor {
    fun isEnableFontSize(): Boolean

    fun isEnableMargin(): Boolean

    fun isEnablePadding(): Boolean

    fun isEnableSpace(): Boolean

    fun isEnableAlignment(): Boolean
}