package com.newbiechen.nbreader.ui.component.book.text.hyphenation

/**
 *  author : newbiechen
 *  date : 2019-11-03 15:37
 *  description :断字信息类
 */

class TextHyphenInfo(length: Int) {
    var mask: BooleanArray = BooleanArray(length - 1)

    fun isHyphenationPossible(position: Int): Boolean {
        return position < mask.size && mask[position]
    }
}