package com.newbiechen.nbreader.uilts.factory

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment

interface MainFragFactory {
    fun createFragment(index: Int): Fragment
    fun createFragmentTabView(context: Context, index: Int): View
    fun getFragmentTitle(context: Context, index: Int): String
    fun getCount(): Int
}