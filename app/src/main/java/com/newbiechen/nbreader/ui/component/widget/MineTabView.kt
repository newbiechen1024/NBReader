package com.newbiechen.nbreader.ui.component.widget

import android.content.Context
import com.newbiechen.nbreader.R

class MineTabView(context: Context) : TabView(context) {
    override fun getImageRes(isChecked: Boolean): Int {
        return if (isChecked) R.drawable.icon_mine_selected else R.drawable.icon_mine_unselected
    }

    override fun onCheckedChange(isChecked: Boolean) {
    }

}