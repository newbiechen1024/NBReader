package com.example.newbiechen.nbreader.ui.component.widget.page.anim

import android.graphics.Canvas
import android.view.View
import com.example.newbiechen.nbreader.ui.component.widget.page.PageBitmapManager
import com.example.newbiechen.nbreader.uilts.LogHelper

/**
 *  author : newbiechen
 *  date : 2019-09-02 11:13
 *  description :
 */

class NonePageAnimation(view: View, pageManager: PageBitmapManager) :
    PageAnimation(view, pageManager) {
    companion object {
        private const val TAG = "NonePageAnimation"
    }

    override fun drawStatic(canvas: Canvas) {
        // 绘制当前页
        canvas.drawBitmap(getFromPage(), 0f, 0f, null)
    }

    override fun drawMove(canvas: Canvas) {
        canvas.drawBitmap(getFromPage(), 0f, 0f, null)
    }

    override fun startAnimInternal() {
        // 预加载 toPage，更新翻到下一页的页面 todo: 放这里似乎不太妥当，下次找个比较好的地方吧
        getToPage()

        // 直接结束动画
        finishAnim()

        mView.postInvalidate()
    }
}