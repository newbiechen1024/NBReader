package com.example.newbiechen.nbreader.ui.component.book.text.util

import com.example.newbiechen.nbreader.uilts.LogHelper
import okhttp3.internal.and
import java.nio.charset.Charset
import java.util.regex.Pattern

/**
 *  author : newbiechen
 *  date : 2019-11-26 16:47
 *  description :章节探测器 (专门给 native 调用处理)
 *
 *  步骤：
 *
 *  1. 从 resouce 读取 pattern 的 xml，并修改文字进行匹配操作
 *  2. 返回给 native 进行对应编码的章节匹配
 *
 *  TODO:如果探测器效率太低，改到 C++ 层做也行
 */

class ChapterDetector private constructor(private val lang: String) {
    // TODO:需要将 pattern 作为 xml 添加到 asset 中，暂时不处理.(拿到之后的错误判断也是一个问题)，可以参考 Android 的 %1$d

    companion object {

        private const val TAG = "ChapterDetector"

        private const val TEST_PATTERN = "^(.{0,8})(%1\$s)([0-9%2\$s]{1,10})([%3\$s])(.{0,30})\$"

        @JvmStatic
        fun createChapterDetector(lang: String): ChapterDetector? {
            return ChapterDetector(lang)
        }

        /**
         * 是否是语言支持
         */
        fun isLangSupport(lang: String) {
            // 待处理
        }

        /**
         * 是否是支持的编码
         */
        fun isEncodingSupport(encoding: String): Boolean {

            // 判断 encoding 是否支持编码
            return try {
                Charset.forName(encoding) != null
            } catch (e: Exception) {
                false
            }
        }

        /**
         * 返回编码的文字
         *
         * @param b
         * @param size
         * @return
         */
        private fun getEncodingStr(b: ByteArray, size: Int): String {
            var stmp = ""
            val sb = StringBuilder("")
            for (n in 0 until size) {
                stmp = Integer.toHexString(b[n] and 0xFF)
                sb.append("\\x") // 传入的数据不可能是 unicode ，所以都是 \x
                sb.append(if (stmp.length == 1) "0$stmp" else stmp)
            }
            return sb.toString().trim()
        }
    }

    //正则表达式章节匹配模式
    /**
     * Native 调用，返回正则表达式
     */
    fun getRegexStr(encoding: String): String {
        var charset: Charset

        try {
            // 获取编码
            charset = Charset.forName(encoding)
        } catch (exception: java.lang.Exception) {
            // TODO:错误信息待处理
            return ""
        }

        val strArry = arrayOf("第", "零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟", "章节回集卷")
        val outStrArr = Array(strArry.size) { "" }
        // 遍历生成 编码
        strArry.forEachIndexed { index, s ->
            var byteArr = s.toByteArray(charset)
            outStrArr[index] = getEncodingStr(byteArr, byteArr.size)
        }

        // kt 需要在数组前加上 *，才能作为可变参数传递给 java
        return String.format(TEST_PATTERN, *outStrArr)
    }
}