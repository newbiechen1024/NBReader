package com.newbiechen.nbreader.ui.component.book.text.util

import java.util.*
import java.util.regex.Pattern

/**
 *  author : newbiechen
 *  date : 2019-10-27 15:30
 *  description :
 */

object TextMiscUtil {
    fun isEmptyString(s: String?): Boolean {
        return s == null || "" == s
    }

    fun matchesIgnoreCase(text: String, lowerCasePattern: String): Boolean {
        return text.length >= lowerCasePattern.length && text.toLowerCase().indexOf(lowerCasePattern) >= 0
    }

    fun join(list: List<String>?, delimiter: String): String {
        if (list == null || list.isEmpty()) {
            return ""
        }
        val builder = StringBuilder()
        var first = true
        for (s in list) {
            if (first) {
                first = false
            } else {
                builder.append(delimiter)
            }
            builder.append(s)
        }
        return builder.toString()
    }

    fun split(str: String?, delimiter: String): List<String> {
        return if (str == null || "" == str) {
            emptyList()
        } else {
            listOf(*str.split(delimiter.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        }
    }

    // splits str on any space symbols, keeps quoted substrings
    fun smartSplit(str: String): List<String> {
        val tokens = LinkedList<String>()
        val m = Pattern.compile("([^\"\\s:;]+|\".+?\")").matcher(str)
        while (m.find()) {
            tokens.add(m.group(1).replace("\"", ""))
        }
        return tokens
    }
}
