package com.newbiechen.nbreader.ui.component.extension

import android.view.Gravity
import android.view.View
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout

/**
 *  author : newbiechen
 *  date : 2019-08-29 17:31
 *  description :
 */

/**
 * 获取 DrawerView 下的 Gravity
 */
fun DrawerLayout.getDrawerView(): View? {
    // 遍历查找 DrawerView 的 gravity
    for (i in 0 until childCount) {
        var child = getChildAt(i)
        // 检测是否是 DrawerView
        if (isDrawerView(child)) {
            return child
        }
    }
    return null
}

fun DrawerLayout.getDrawerGravity(): Int? {
    val drawerView = getDrawerView() ?: return null
    return (drawerView.layoutParams as DrawerLayout.LayoutParams).gravity
}

fun DrawerLayout.openDrawer() {
    var gravity = getDrawerGravity()
    if (gravity != null && !isDrawerOpen(gravity)) {
        openDrawer(gravity)
    }
}

fun DrawerLayout.closeDrawer() {
    var gravity = getDrawerGravity()
    if (gravity != null && isDrawerOpen(gravity)) {
        closeDrawer(gravity)
    }
}

fun DrawerLayout.isDrawerOpen(): Boolean {
    var gravity = getDrawerGravity()
    return if (gravity != null) isDrawerOpen(gravity) else false
}

private fun isDrawerView(child: View): Boolean {
    val gravity = (child.layoutParams as DrawerLayout.LayoutParams).gravity

    val absGravity = GravityCompat.getAbsoluteGravity(
        gravity,
        ViewCompat.getLayoutDirection(child)
    )
    if (absGravity and Gravity.LEFT != 0) {
        // This child is a left-edge drawer
        return true
    }
    return absGravity and Gravity.RIGHT != 0
}