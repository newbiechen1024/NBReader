package com.newbiechen.nbreader.ui.component.widget.page.action

import android.view.MotionEvent

/**
 *  author : newbiechen
 *  date : 2020/3/5 4:04 PM
 *  description ：运动事件
 */

enum class MotionType {
    PRESS,        // 按下事件
    LONG_PRESS,   // 长按事件
    MOVE,         // 移动事件
    RELEASE,      // 释放事件
    CANCEL,       // 取消事件
    SINGLE_TAP,   // 单击事件
    DOUBLE_TAP,   // 双击事件
}

data class MotionAction(
    val type: MotionType,
    val event: MotionEvent
) : PageAction {
    companion object {
        fun obtain(other: MotionAction): MotionAction {
            val newType = other.type
            val newEvent = MotionEvent.obtain(other.event)
            return MotionAction(newType, newEvent)
        }
    }

    /**
     * 对于 obtain() 复制的 action，需要调用该方法回收
     */
    fun recycle() {
        // 需要回收 event
        event.recycle()
    }
}