package com.newbiechen.nbreader.ui.component.widget.page.action

import android.view.MotionEvent
import com.newbiechen.nbreader.ui.component.widget.page.PageType

/**
 *  author : newbiechen
 *  date : 2019-09-02 10:54
 *  description :页面行为的集合
 */

// 页面行为标记
interface PageAction

// TODO:将点击事件设为枚举，并实现从缓冲池中获取,仿照 MotionEvent
enum class TouchActionType{
    PRESS,
    MOVE,
    RELEASE,
    SINGLE_TAP,
    CANCEL
}

// 按下事件
data class PressAction(
    val event: MotionEvent
) : PageAction

// 移动事件
data class MoveAction(
    val event: MotionEvent
) : PageAction

// 释放事件
data class ReleaseAction(
    val event: MotionEvent
) : PageAction

// 单击事件
data class TapAction(
    val event: MotionEvent
) : PageAction

// 取消点击事件
class CancelAction : PageAction

// 翻页事件
data class TurnPageAction(
    val pageType: PageType
) : PageAction

// 点击页面菜单事件
class TapMenuAction : PageAction