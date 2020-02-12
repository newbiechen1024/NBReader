package com.newbiechen.nbreader.ui.component.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter

/**
 *  author : newbiechen
 *  date : 2020-02-13 00:27
 *  description :
 */

object TextViewBinding {
    @BindingAdapter("app:selected")
    @JvmStatic
    fun TextView.setTextSelected(isSelected: Boolean) {
        setSelected(isSelected)
    }
}