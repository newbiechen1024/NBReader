package com.example.newbiechen.nbreader.ui.component.extension

import androidx.appcompat.widget.Toolbar
import com.example.newbiechen.nbreader.uilts.LogHelper
import com.example.newbiechen.nbreader.uilts.SystemBarUtil

/**
 *  author : newbiechen
 *  date : 2019-08-30 11:20
 *  description :
 */

/**
 * 解决 toolbar 顶到 statusBar 的问题
 */
fun Toolbar.overlayStatusBar() {
    // 获取当前 statusbar 的高度
    var statusBarHeight = SystemBarUtil.getStatusBarHeight(context)
    // 重新计算 toolbar 的高度
    var resultPaddingTop = statusBarHeight + paddingTop
    // 重置高度
    layoutParams.height += statusBarHeight
    // 重置 padding
    setPadding(paddingLeft, resultPaddingTop, paddingRight, paddingBottom)
}