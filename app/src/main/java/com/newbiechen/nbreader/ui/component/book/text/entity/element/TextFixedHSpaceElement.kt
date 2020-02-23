package com.newbiechen.nbreader.ui.component.book.text.entity.element

/**
 *  author : newbiechen
 *  date : 2019-11-07 13:58
 *  description :
 */

class TextFixedHSpaceElement(val length: Int) : TextElement() {
    companion object {
        // 缓存对应长度的 HSpace
        private val sCacheHSpaceList = arrayOfNulls<TextElement>(20)

        fun getElement(len: Int): TextElement {
            return if (len < 20) {
                var cached: TextElement? = sCacheHSpaceList[len]
                if (cached == null) {
                    cached = TextFixedHSpaceElement(len)
                    sCacheHSpaceList[len] = cached
                }
                cached
            } else {
                TextFixedHSpaceElement(len)
            }
        }
    }
}