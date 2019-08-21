package com.example.newbiechen.nbreader.uilts.factory

import android.content.Context
import androidx.fragment.app.Fragment

/**
 *  author : newbiechen
 *  date : 2019-08-17 12:27
 *  description :
 */

interface FragmentFactory {
    fun getTitle(context: Context, index: Int): String?
    fun createFragment(title: String): Fragment?
}