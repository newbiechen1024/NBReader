package com.newbiechen.nbreader.ui.component.widget.page

import com.newbiechen.nbreader.ui.component.book.text.entity.TextFixedPosition
import com.newbiechen.nbreader.ui.component.book.text.processor.PagePosition
import com.newbiechen.nbreader.ui.component.book.text.processor.PageProgress
import com.newbiechen.nbreader.ui.component.book.text.processor.TextModel
import com.newbiechen.nbreader.ui.component.widget.page.text.PageActionListener

/**
 *  author : newbiechen
 *  date : 2020/3/7 7:59 PM
 *  description :
 */
interface TextController {

    /**
     * 初始化控制器
     */
    fun initController(textModel: TextModel)

    /**
     * 设置页面监听
     */
    fun setPageListener(onPageListener: OnPageListener)

    /**
     * 页面点击事件监听
     */
    fun setPageActionListener(pageActionListener: PageActionListener)

    /**
     * 进行翻页操作
     */
    fun skipPage(type: PageType)

    /**
     * 翻章操作
     */
    fun skipChapter(type: PageType)

    /**
     * 跳转章节操作
     * @param index:章节索引
     */
    fun skipChapter(index: Int)

    /**
     * 进行跳转页面操作
     */
    fun skipPage(position: TextFixedPosition)

    /**
     * 是否页面存在
     */
    fun hasPage(type: PageType): Boolean

    /**
     * 是否章节存在
     */
    fun hasChapter(type: PageType): Boolean

    fun hasChapter(index: Int): Boolean

    /**
     * 获取页面位置
     */
    fun getPagePosition(pageType: PageType): PagePosition?

    /**
     * 获取页面进度
     */
    fun getPageProgress(type: PageType): PageProgress?

    /**
     * 获取页面数
     */
    fun getPageCount(pageType: PageType): Int
}