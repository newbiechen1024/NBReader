package com.newbiechen.nbreader.ui.component.widget.page.anim

import android.graphics.Canvas
import android.view.View

/**
 *  author : newbiechen
 *  date : 2019-09-02 11:13
 *  description :
 */

class NonePageAnimation(view: View, pageManager: IPageAnimCallback) :
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

    override fun startAnim() {
        // TODO:预加载 toPage，更新翻到下一页的页面 (忘记这干啥用的了)
        // getToPage()

        // 直接结束动画
        finishAnim()
    }
}