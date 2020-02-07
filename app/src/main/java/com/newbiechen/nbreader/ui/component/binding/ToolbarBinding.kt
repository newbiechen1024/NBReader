package com.newbiechen.nbreader.ui.component.binding

import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter

object ToolbarBinding {

    @BindingAdapter("app:menuPos")
    @JvmStatic
    fun Toolbar.setCurrentMenu(position: Int) {
    }
}