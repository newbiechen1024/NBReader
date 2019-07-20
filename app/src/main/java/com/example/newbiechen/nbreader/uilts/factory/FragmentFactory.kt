package com.example.newbiechen.nbreader.uilts.factory

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment

interface FragmentFactory {
    fun createFragment(index: Int): Fragment
    fun createFragmentTabView(context: Context, index: Int): View
    fun getCount(): Int
}