package com.example.newbiechen.nbreader.uilts

import android.content.Context
import com.example.newbiechen.nbreader.R
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

/**
 *  author : newbiechen
 *  date : 2019-08-06 15:29
 *  description :
 */

object StringUtil {

    fun size2Str(size: Long): String {
        if (size <= 0) return "0"
        val units = arrayOf("B", "KB", "M", "G", "T")
        //计算单位的，原理是利用lg,公式是 lg(1024^n) = nlg(1024)，最后 nlg(1024)/lg(1024) = n。
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        //计算原理是，size/单位值。单位值指的是:比如说b = 1024,KB = 1024^2
        return DecimalFormat("#,##0.##").format(
            size / 1024.0.pow(digitGroups.toDouble())
        ) + " " + units[digitGroups]
    }
}