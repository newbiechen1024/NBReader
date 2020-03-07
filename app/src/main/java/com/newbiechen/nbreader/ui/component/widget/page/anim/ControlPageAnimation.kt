package com.newbiechen.nbreader.ui.component.widget.page.anim

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import com.newbiechen.nbreader.ui.component.widget.page.PageType
import com.newbiechen.nbreader.ui.component.widget.page.action.MotionAction
import com.newbiechen.nbreader.ui.component.widget.page.text.TextPageManager

/**
 *  author : newbiechen
 *  date : 2020/3/7 10:04 AM
 *  description :手动控制翻页动画
 */

class ControlPageAnimation(view: View, pageManager: TextPageManager) :
    TextPageAnimation(view, pageManager) {

    // 视图的宽高
    private var mViewWidth = 0
    private var mViewHeight = 0

    private var mCurPageType: PageType = PageType.CURRENT

    override fun setViewPort(w: Int, h: Int) {
        if (w == 0 || h == 0 || mViewWidth == w || mViewHeight == h) {
            return
        }

        mViewWidth = w
        mViewHeight = h

        // 通知页面管理器
        mPageManager.onPageSizeChanged(w, h)

        // 通知取消动画
        abortAnim()
    }

    override fun onTouchEvent(action: MotionAction): Boolean {
        return false
    }

    override fun draw(canvas: Canvas) {
        canvas.drawPicture(mPageManager.getPage(mCurPageType))
    }

    override fun startAnim() {
        // 不处理
    }

    override fun abortAnim() {
        // 不处理
    }

    override fun computeScroll() {
        // 不处理
    }

    /**
     * 手动设置绘制的页面
     */
    fun setDrawPage(pageType: PageType) {
        mCurPageType = pageType
    }

    fun turnPage(pageType: PageType) {
        if (mPageManager.hasPage(pageType)) {
            mPageManager.turnPage(pageType)
        }
    }
}