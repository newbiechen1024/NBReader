package com.example.newbiechen.nbreader.uilts

import android.content.Context
import com.example.newbiechen.nbreader.R
import java.text.DecimalFormat

/**
 *  author : newbiechen
 *  date : 2019-08-16 15:50
 *  description :
 */

object NumberUtil {

    private const val TEN_THOUSAND = 10000

    /**
     * @param count 将数值缩小
     */
    fun convertNumber(context: Context, count: Long): String {
        val df = DecimalFormat("#")
        if (count < TEN_THOUSAND) {
            return count.toString()
        } else if (count >= TEN_THOUSAND) {
            val countNumber = count / TEN_THOUSAND
            return df.format(countNumber) + context.resources.getString(R.string.common_ten_thousand)
        }
        return count.toString()
    }
}