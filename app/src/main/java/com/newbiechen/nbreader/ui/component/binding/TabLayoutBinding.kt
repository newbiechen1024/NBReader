package com.newbiechen.nbreader.ui.component.binding

import androidx.databinding.BindingAdapter
import com.newbiechen.nbreader.ui.component.widget.TabView
import com.google.android.material.tabs.TabLayout

object TabLayoutBinding {

    @BindingAdapter("app:currentPos")
    @JvmStatic
    fun TabLayout.setCurrentItem(index: Int) {
        var tabView: TabView?
        for (i in 0 until tabCount) {
            tabView = getTabAt(i)?.customView as TabView?
            if (i == selectedTabPosition) {
                getTabAt(i)!!.select()
                tabView?.setChecked(true)
            } else {
                tabView?.setChecked(false)
            }
        }
    }
}