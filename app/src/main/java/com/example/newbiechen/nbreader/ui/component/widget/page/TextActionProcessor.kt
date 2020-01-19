package com.example.newbiechen.nbreader.ui.component.widget.page

import android.graphics.Rect
import com.example.newbiechen.nbreader.uilts.TouchProcessor

/**
 *  author : newbiechen
 *  date : 2019-08-30 16:20
 *  description :文本事件分发处理器，控制、加载、翻页、绘制、点击事件
 */

typealias PageActionListener = (action: Any) -> Unit

class PageActionProcessor : TouchProcessor.OnTouchListener {

    private var mPageActionList = mutableSetOf<PageActionListener>()

    private var mPageWidth = 0
    private var mPageHeight = 0
    private var mMenuRect = Rect()

    companion object {
        private const val TAG = "PageController"
    }

    fun addPageActionListener(pageAction: PageActionListener) {
        mPageActionList.add(pageAction)
    }

    fun removePageActionListener(pageAction: PageActionListener) {
        mPageActionList.remove(pageAction)
    }

    override fun onPress(x: Int, y: Int) {
        dispatchAction(PressPageAction(x, y))
    }

    override fun onMove(x: Int, y: Int) {
        dispatchAction(MovePageAction(x, y))
    }

    override fun onRelease(x: Int, y: Int) {
        dispatchAction(ReleasePageAction(x, y))
    }

    override fun onLongPress(x: Int, y: Int) {

    }

    override fun onMoveAfterLongPress(x: Int, y: Int) {
    }

    override fun onReleaseAfterLongPress(x: Int, y: Int) {
    }

    override fun onSingleTap(x: Int, y: Int) {
        val action: Any = if (mMenuRect.contains(x, y)) {
            ReadMenuAction()
        } else {
            // 检测当前点击区域是否
            TapPageAction(x, y)
        }

        dispatchAction(action)
    }

    override fun onDoubleTap(x: Int, y: Int) {
    }

    override fun onCancelTap() {
        // TODO:处理 cancel 的情况
    }

    internal fun dispatchAction(action: Any) {
        mPageActionList.forEach {
            it(action)
        }
    }

    fun setViewPort(w: Int, h: Int) {
        mPageWidth = w
        mPageHeight = h
        mMenuRect.set((w / 5), (h / 3), w * 4 / 5, h * 2 / 3)
    }
}