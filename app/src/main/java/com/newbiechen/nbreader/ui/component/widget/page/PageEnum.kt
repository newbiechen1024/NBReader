package com.newbiechen.nbreader.ui.component.widget.page

/**
 *  author : newbiechen
 *  date : 2019-08-30 14:58
 *  description :Page 使用到的枚举类集合
 */

/**
 * 翻页动画类型
 */
enum class PageAnimType {
    SIMULATION, SCROLL, COVER, SLIDE, NONE
}

enum class TextAnimType {
    CONTROL, SCROLL
}

/**
 * 页面的类型
 */
enum class PageType {
    PREVIOUS, CURRENT, NEXT;

    fun getNext(): PageType? {
        return when (this) {
            PREVIOUS -> CURRENT
            CURRENT -> NEXT
            else -> null
        }
    }

    fun getPrevious(): PageType? {
        return when (this) {
            NEXT -> CURRENT
            CURRENT -> PREVIOUS
            else -> null
        }
    }
}