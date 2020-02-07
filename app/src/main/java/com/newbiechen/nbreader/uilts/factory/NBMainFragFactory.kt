package com.newbiechen.nbreader.uilts.factory

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import com.newbiechen.nbreader.R
import com.newbiechen.nbreader.ui.component.widget.BookShelfTabView
import com.newbiechen.nbreader.ui.component.widget.FindTabView
import com.newbiechen.nbreader.ui.component.widget.MineTabView
import com.newbiechen.nbreader.ui.page.bookshelf.BookShelfFragment
import com.newbiechen.nbreader.ui.page.find.FindFragment
import com.newbiechen.nbreader.ui.page.mine.MineFragment
import javax.inject.Inject

class NBMainFragFactory @Inject constructor() : MainFragFactory {
    override fun getFragmentTitle(context: Context, index: Int): String {
        return context.getString(
            when (index) {
                0 -> R.string.common_book_shelf
                1 -> R.string.common_find
                else -> R.string.common_personal_center
            }
        )
    }

    override fun createFragment(index: Int): Fragment {
        return when (index) {
            0 -> BookShelfFragment.newInstance()
            1 -> FindFragment.newInstance()
            else -> MineFragment.newInstance()
        }
    }

    override fun createFragmentTabView(context: Context, index: Int): View {
        return when (index) {
            0 -> BookShelfTabView(context)
            1 -> FindTabView(context)
            else -> MineTabView(context)
        }
    }

    override fun getCount(): Int = 3
}