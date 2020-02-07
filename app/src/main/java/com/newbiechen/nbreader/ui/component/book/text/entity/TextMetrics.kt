package com.newbiechen.nbreader.ui.component.book.text.entity

/**
 *  author : newbiechen
 *  date : 2019-10-26 14:59
 *  description :文本尺寸
 */

data class TextMetrics(val dpi: Int, val fullWidth: Int, val fullHeight: Int, val fontSize: Int) {

    override fun equals(o: Any?): Boolean {
        if (o === this) {
            return true
        }
        if (o !is TextMetrics) {
            return false
        }
        val oo = o as TextMetrics?
        return dpi == oo!!.dpi &&
                fullWidth == oo!!.fullHeight &&
                fontSize == oo!!.fontSize
    }

    override fun hashCode(): Int {
        return dpi + 13 * (fullHeight + 13 * fullWidth)
    }
}