package com.newbiechen.nbreader.ui.component.widget.page

import com.newbiechen.nbreader.ui.component.book.text.engine.PagePosition
import com.newbiechen.nbreader.ui.component.book.text.engine.PageProgress

/**
 *  author : newbiechen
 *  date : 2020/3/7 8:01 PM
 *  description :
 */
interface OnPageListener {
    // 通知页面准备
    fun onPreparePage(pagePosition: PagePosition, pageProgress: PageProgress)

    // 通知页面改变
    fun onPageChange(pagePosition: PagePosition, pageProgress: PageProgress)
}