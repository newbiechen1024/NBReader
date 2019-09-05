package com.example.newbiechen.nbreader.ui.component.widget.page

/**
 *  author : newbiechen
 *  date : 2019-09-02 10:54
 *  description :页面行为的集合
 */

// 点击页面事件
data class PressPageAction(
    val x: Int,
    val y: Int
)

data class MovePageAction(
    val x: Int,
    val y: Int
)

data class ReleasePageAction(
    val x: Int,
    val y: Int
)