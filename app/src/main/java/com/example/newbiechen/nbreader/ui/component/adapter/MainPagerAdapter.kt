package com.example.newbiechen.nbreader.ui.component.adapter

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.newbiechen.nbreader.uilts.factory.FragmentFactory

class MainPagerAdapter(fm: FragmentManager, var factory: FragmentFactory) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return factory.createFragment(position)
    }

    override fun getCount(): Int = factory.getCount()
}
