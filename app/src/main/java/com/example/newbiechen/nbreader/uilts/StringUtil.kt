package com.example.newbiechen.nbreader.uilts

import android.content.Context
import com.example.newbiechen.nbreader.R

/**
 *  author : newbiechen
 *  date : 2019-08-06 15:29
 *  description :
 */

object StringUtil {

    fun number2Str(context: Context, count: Int): String {
        return if (count >= 10000) {
            val result = count.toFloat() / 10000f
            context.getString(R.string.ten_thousand, result)
        } else {
            count.toString()
        }
    }
}