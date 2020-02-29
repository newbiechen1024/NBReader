package com.newbiechen.nbreader.ui.component.book.text.util

import com.newbiechen.nbreader.ui.component.book.text.entity.textstyle.TextDecoratedStyleDescription
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.HashMap
import java.util.LinkedHashMap

/**
 *  author : newbiechen
 *  date : 2019-10-26 17:46
 *  description :CSS 文件读取器
 */

class TextCSSReader {
    private enum class State {
        EXPECT_SELECTOR,
        EXPECT_OPEN_BRACKET,
        EXPECT_NAME,
        EXPECT_VALUE,
        READ_COMMENT
    }

    private var mState: State? = null
    private var mSavedState: State? = null
    private var mCurrentMap: MutableMap<String, String>? = null
    private var mSelector: String? = null
    private var mName: String? = null
    // int 表示 description 对应的 fbreader-id
    // TextDecoratedStyleDescription 表示具体样式
    private var mDescriptionMap: MutableMap<Int, TextDecoratedStyleDescription>? = null

    fun read(inputStream: InputStream): Map<Int, TextDecoratedStyleDescription> {
        mDescriptionMap = LinkedHashMap()
        mState = State.EXPECT_SELECTOR

        try {
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?

            do {
                line = reader.readLine()
                if (line == null) {
                    break
                }
                for (token in TextMiscUtil.smartSplit(line)) {
                    processToken(token)
                }
            } while (line != null)
        } catch (e: IOException) {
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                }
            }
        }
        return mDescriptionMap!!
    }

    private fun processToken(token: String) {
        if (mState != State.READ_COMMENT && token.startsWith("/*")) {
            mSavedState = mState
            mState = State.READ_COMMENT
            return
        }

        when (mState) {
            State.READ_COMMENT -> if (token.endsWith("*/")) {
                mState = mSavedState
            }

            State.EXPECT_SELECTOR -> {
                mSelector = token
                mState = State.EXPECT_OPEN_BRACKET
            }

            State.EXPECT_OPEN_BRACKET -> if ("{" == token) {
                mCurrentMap = HashMap()
                mState = State.EXPECT_NAME
            }

            State.EXPECT_NAME -> if ("}" == token) {
                if (mSelector != null) {
                    try {
                        mDescriptionMap!![Integer.valueOf(mCurrentMap!!["fbreader-id"])] =
                            TextDecoratedStyleDescription(mSelector!!, mCurrentMap!!)
                    } catch (e: Exception) {
                        // ignore
                    }
                }
                mState = State.EXPECT_SELECTOR
            } else {
                mName = token
                mState = State.EXPECT_VALUE
            }

            State.EXPECT_VALUE -> {
                if (mCurrentMap != null && mName != null) {
                    mCurrentMap!![mName!!] = token
                }
                mState = State.EXPECT_NAME
            }
        }
    }
}