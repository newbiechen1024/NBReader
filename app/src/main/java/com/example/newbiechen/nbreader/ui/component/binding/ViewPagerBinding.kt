package com.example.newbiechen.nbreader.ui.component.binding

import androidx.databinding.BindingAdapter
import androidx.viewpager.widget.ViewPager

object ViewPagerBinding {

    @BindingAdapter("app:currentPos")
    @JvmStatic
    fun ViewPager.setCurrentItem(currentPos: Int) {
        setCurrentItem(currentPos, true)
    }
}