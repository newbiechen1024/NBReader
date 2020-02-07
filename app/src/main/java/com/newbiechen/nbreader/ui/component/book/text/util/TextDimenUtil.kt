package com.newbiechen.nbreader.ui.component.book.text.util

import android.content.Context
import android.util.DisplayMetrics

/**
 *  author : newbiechen
 *  date : 2019-11-07 15:18
 *  description :文本尺寸工具
 */

object TextDimenUtil {

    fun getDisplayDPI(context: Context): Int {
        val metrics = getMetrics(context)
        return (160 * metrics.density).toInt()
    }

    private fun getMetrics(context: Context): DisplayMetrics {
        return context.resources.displayMetrics
    }
}