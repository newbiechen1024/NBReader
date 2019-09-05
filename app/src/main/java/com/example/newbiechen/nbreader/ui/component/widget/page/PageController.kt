package com.example.newbiechen.nbreader.ui.component.widget.page

import android.graphics.Bitmap
import com.example.newbiechen.nbreader.uilts.TouchProcessor

/**
 *  author : newbiechen
 *  date : 2019-08-30 16:20
 *  description :页面控制器，控制、加载、翻页、绘制、点击事件
 *
 *  1. 解决书籍解析问题。
 *  2. 绘制书籍。
 *  3. 根据点击的位置处理事件分发。
 *
 *  比较好的话，就是将 PageController 分为：IPageController 接口 -> PageTextController -> PageTouchController
 *  不过我还没有全部理解，所以比较好的方式还是先先到一起，然后慢慢分割。
 */

class PageController : TouchProcessor.OnTouchListener, PageManager.OnPageListener {

    private var mPageActionList = mutableSetOf<PageActionListener>()


    fun addPageActionListener(pageAction: PageActionListener) {
        mPageActionList.add(pageAction)
    }

    fun removePageActionListener(pageAction: PageActionListener) {
        mPageActionList.remove(pageAction)
    }

    override fun hasPage(type: PageType): Boolean {
        return false
    }

    override fun drawPage(bitmap: Bitmap, type: PageType) {

    }

    override fun onPress(x: Int, y: Int) {

    }

    override fun onMove(x: Int, y: Int) {
    }

    override fun onRelease(x: Int, y: Int) {
    }

    override fun onLongPress(x: Int, y: Int) {
    }

    override fun onMoveAfterLongPress(x: Int, y: Int) {
    }

    override fun onReleaseAfterLongPress(x: Int, y: Int) {
    }

    override fun onSingleTap(x: Int, y: Int) {
    }

    override fun onDoubleTap(x: Int, y: Int) {
    }

    override fun onCancelTap() {
    }

    interface PageActionListener {
        fun onPageAction(action: Any)
    }
}