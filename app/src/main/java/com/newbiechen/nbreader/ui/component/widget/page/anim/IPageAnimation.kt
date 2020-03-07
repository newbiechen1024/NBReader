package com.newbiechen.nbreader.ui.component.widget.page.anim

import android.graphics.Canvas
import android.view.MotionEvent
import com.newbiechen.nbreader.ui.component.widget.page.action.MotionAction

/**
 *  author : newbiechen
 *  date : 2020/3/7 9:31 AM
 *  description :
 */
interface IPageAnimation {
    /**
     * 设置视口
     */
    fun setViewPort(w: Int, h: Int)

    /**
     * 传递点击行为
     */
    fun onTouchEvent(action: MotionAction): Boolean

    fun draw(canvas: Canvas)

    fun startAnim()

    fun abortAnim()

    fun computeScroll()
}