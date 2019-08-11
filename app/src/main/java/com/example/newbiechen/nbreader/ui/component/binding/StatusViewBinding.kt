package com.example.newbiechen.nbreader.ui.component.binding

import androidx.databinding.BindingAdapter
import com.example.newbiechen.nbreader.ui.component.widget.StatusView

/**
 *  author : newbiechen
 *  date : 2019-08-10 17:40
 *  description :
 */

object StatusViewBinding {

    @BindingAdapter("app:curStatus")
    @JvmStatic
    fun StatusView.setViewStatus(status: Int) {
        setStatus(status)
    }
}