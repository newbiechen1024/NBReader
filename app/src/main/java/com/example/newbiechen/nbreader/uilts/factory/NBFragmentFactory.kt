package com.example.newbiechen.nbreader.uilts.factory

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import com.example.newbiechen.nbreader.ui.component.widget.BookShelfTabView
import com.example.newbiechen.nbreader.ui.component.widget.FindTabView
import com.example.newbiechen.nbreader.ui.component.widget.MineTabView
import com.example.newbiechen.nbreader.ui.page.bookshelf.BookShelfFragment
import com.example.newbiechen.nbreader.ui.page.category.FindFragment
import com.example.newbiechen.nbreader.ui.page.mine.MineFragment
import javax.inject.Inject

class NBFragmentFactory @Inject constructor() : FragmentFactory {
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