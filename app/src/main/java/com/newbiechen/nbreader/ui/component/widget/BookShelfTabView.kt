package com.newbiechen.nbreader.ui.component.widget

import android.content.Context
import com.newbiechen.nbreader.R

class BookShelfTabView(context: Context) : TabView(context) {

    override fun getImageRes(isChecked: Boolean): Int {
        return if (isChecked) R.drawable.icon_book_shelf_selected
        else R.drawable.icon_book_shelf_unselected
    }

    override fun onCheckedChange(isChecked: Boolean) {
        // 暂时不处理
    }
}