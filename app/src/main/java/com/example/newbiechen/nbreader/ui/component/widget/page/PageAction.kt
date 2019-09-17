package com.example.newbiechen.nbreader.ui.component.widget.page

/**
 *  author : newbiechen
 *  date : 2019-09-02 10:54
 *  description :页面行为的集合
 */

// 下压页面事件
data class PressPageAction(
    val x: Int,
    val y: Int
)

// 移动页面事件
data class MovePageAction(
    val x: Int,
    val y: Int
)

// 释放页面事件
data class ReleasePageAction(
    val x: Int,
    val y: Int
)

// 单击页面事件
data class TapPageAction(
    val x: Int,
    val y: Int
)

// 翻页事件
data class TurnPageAction(
    val pageType: PageType
)

// 点击页面菜单事件
class ReadMenuAction