package com.example.newbiechen.nbreader.ui.component.book.text.config

import android.content.Context
import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextMetrics
import com.example.newbiechen.nbreader.ui.component.book.text.entity.textstyle.TextStyle

/**
 *  author : newbiechen
 *  date : 2019-10-26 16:00
 *  description :文本样式默认实现类
 */

class DefaultTextStyle private constructor(context: Context) : TextStyle(null) {

    companion object {
        private var sInstance: DefaultTextStyle? = null

        fun getInstance(context: Context): DefaultTextStyle {
            if (sInstance == null) {
                sInstance = DefaultTextStyle(context.applicationContext)
            }
            return sInstance!!
        }
    }

    private var mSharedPrefs = TextStyleSharedPrefs.getInstance(context)

    fun setFontSize(fontSize: Int) {
        mSharedPrefs.textSize = fontSize
    }

    fun getFontSize(): Int {
        return mSharedPrefs.textSize
    }

    override fun getFontSize(metrics: TextMetrics): Int {
        return mSharedPrefs.textSize
    }

    override fun isBold(): Boolean {
        return false
    }

    override fun isItalic(): Boolean {
        return false
    }

    override fun isUnderline(): Boolean {
        return false
    }

    override fun isStrikeThrough(): Boolean {
        return false
    }

    override fun getLeftMargin(metrics: TextMetrics): Int {
        return 0
    }

    override fun getRightMargin(metrics: TextMetrics): Int {
        return 0
    }

    override fun getLeftPadding(metrics: TextMetrics): Int {
        return 0
    }

    override fun getRightPadding(metrics: TextMetrics): Int {
        return 0
    }

    override fun getFirstLineIndent(metrics: TextMetrics): Int {
        return 0
    }

    override fun getLineSpacePercent(): Int {
        return 0
    }

    override fun getVerticalAlign(metrics: TextMetrics): Int {
        return 0
    }

    override fun isVerticallyAligned(): Boolean {
        return false
    }

    override fun getSpaceBefore(metrics: TextMetrics): Int {
        return 0
    }

    override fun getSpaceAfter(metrics: TextMetrics): Int {
        return 0
    }

    override fun getAlignment(): Byte {
        return 0.toByte()
    }

    override fun allowHyphenations(): Boolean {
        return true
    }
}