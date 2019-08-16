package com.example.newbiechen.nbreader.uilts

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 *  author : newbiechen
 *  date : 2019-08-16 15:48
 *  description :
 */
object DateUtil {

    val FORMAT_BOOK_DATE = "yyyy-MM-dd'T'HH:mm:ss"
    val FORMAT_TIME = "HH:mm"
    val FORMAT_FILE_DATE = "yyyy-MM-dd"

    private const val TAG = "StringUtils"
    private const val HOUR_OF_DAY = 24
    private const val DAY_OF_YESTERDAY = 2
    private const val TIME_UNIT = 60

    //将时间转换成日期
    fun dateConvert(time: Long, pattern: String): String {
        val date = Date(time)
        val format = SimpleDateFormat(pattern)

        return format.format(date)
    }

    //将日期转换成昨天、今天、明天
    fun dateConvert(source: String, pattern: String): String {
        val format = SimpleDateFormat(pattern)
        val calendar = Calendar.getInstance()
        try {
            val date = format.parse(source)
            val curTime = calendar.timeInMillis
            calendar.time = date
            //将MISC 转换成 sec
            val difSec = Math.abs((curTime - date.time) / 1000)
            val difMin = difSec / 60
            val difHour = difMin / 60
            val difDate = difHour / 60
            val oldHour = calendar.get(Calendar.HOUR)
            //如果没有时间
            if (oldHour == 0) {
                //比日期:昨天今天和明天
                if (difDate == 0L) {
                    return "今天"
                } else if (difDate < DAY_OF_YESTERDAY) {
                    return "昨天"
                } else {
                    val convertFormat = SimpleDateFormat("yyyy-MM-dd")
                    return convertFormat.format(date)
                }
            }

            return if (difSec < TIME_UNIT) {
                difSec.toString() + "秒前"
            } else if (difMin < TIME_UNIT) {
                difMin.toString() + "分钟前"
            } else if (difHour < HOUR_OF_DAY) {
                difHour.toString() + "小时前"
            } else if (difDate < DAY_OF_YESTERDAY) {
                "昨天"
            } else {
                val convertFormat = SimpleDateFormat("yyyy-MM-dd")
                convertFormat.format(date)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ""
    }
}