package com.example.newbiechen.nbreader.ui.component.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.newbiechen.nbreader.uilts.factory.MainFragFactory

class MainPagerAdapter(fm: FragmentManager, var factory: MainFragFactory) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return factory.createFragment(position)
    }

    override fun getCount(): Int = factory.getCount()
}
