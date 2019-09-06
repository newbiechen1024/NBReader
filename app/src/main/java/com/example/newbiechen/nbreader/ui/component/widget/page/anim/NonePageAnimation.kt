package com.example.newbiechen.nbreader.ui.component.widget.page.anim

import android.graphics.Canvas
import android.view.View
import com.example.newbiechen.nbreader.ui.component.widget.page.PageManager

/**
 *  author : newbiechen
 *  date : 2019-09-02 11:13
 *  description :
 */

class NonePageAnimation(view: View, pageManager: PageManager) : PageAnimation(view, pageManager) {

    override fun drawStatic(canvas: Canvas) {
        // 绘制当前页
        canvas.drawBitmap(getFromPage(), 0f, 0f, null)
    }

    override fun drawMove(canvas: Canvas) {
        canvas.drawBitmap(getToPage(), 0f, 0f, null)
    }

    override fun startAnimInternal(isCancelAnim: Boolean) {
        super.startAnimInternal(isCancelAnim)
        // TODO:假装滑动  ==> 不知道有没有效果
        mScroller.startScroll(0, 0, 0, 0, 100)
    }
}