package com.newbiechen.nbreader.ui.component.widget.page.anim

import android.graphics.Bitmap
import com.newbiechen.nbreader.ui.component.widget.page.PageType

/**
 *  author : newbiechen
 *  date : 2020-01-26 16:13
 *  description :页面动画回调
 */

interface IPageAnimCallback {
    /**
     * 动画页面大小改变
     */
    fun onPageSizeChanged(w: Int, h: Int)

    /**
     * 是否页面存在
     */
    fun hasPage(type: PageType): Boolean

    /**
     * 获取页面
     */
    fun getPage(type: PageType): Bitmap

    /**
     * 请求翻页
     */
    fun turnPage(type: PageType)
}