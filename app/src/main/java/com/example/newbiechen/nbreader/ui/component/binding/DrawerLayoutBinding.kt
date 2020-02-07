package com.example.newbiechen.nbreader.ui.component.binding

import android.view.Gravity
import android.view.View
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.databinding.BindingAdapter
import androidx.drawerlayout.widget.DrawerLayout
import com.example.newbiechen.nbreader.uilts.LogHelper

/**
 *  author : newbiechen
 *  date : 2019-08-29 16:50
 *  description :
 */

object DrawerLayoutBinding {

    /**
     * 设置抽屉是否显示
     */
    @BindingAdapter("app:drawerVisibility")
    @JvmStatic
    fun DrawerLayout.setDrawerVisibility(isVisible: Boolean?) {
        if (isVisible == null) {
            return
        }

        // 遍历查找 DrawerView 的 gravity
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            // 检测是否是 DrawerView
            if (isDrawerView(child)) {
                // 如果显示状态相同，则不处理
                if (isDrawerOpen(child) != isVisible) {
                    val gravity = (child.layoutParams as DrawerLayout.LayoutParams).gravity
                    if (isVisible) {
                        openDrawer(gravity)
                    } else {
                        closeDrawer(gravity)
                    }
                }
                break
            }
        }
    }

    private fun isDrawerView(child: View): Boolean {
        val gravity = (child.layoutParams as DrawerLayout.LayoutParams).gravity
        val absGravity = GravityCompat.getAbsoluteGravity(
            gravity,
            ViewCompat.getLayoutDirection(child)
        )
        if (absGravity and GravityCompat.START != 0) {
            // This child is a left-edge drawer
            return true
        }
        return absGravity and GravityCompat.END != 0
    }
}