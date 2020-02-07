package com.newbiechen.nbreader.ui.component.widget

import android.content.Context
import com.newbiechen.nbreader.R

class FindTabView(context: Context) : TabView(context) {

    override fun getImageRes(isChecked: Boolean): Int {
        return if (isChecked) R.drawable.icon_find_selected else R.drawable.icon_find_unselected
    }

    override fun onCheckedChange(isChecked: Boolean) {
    }

}