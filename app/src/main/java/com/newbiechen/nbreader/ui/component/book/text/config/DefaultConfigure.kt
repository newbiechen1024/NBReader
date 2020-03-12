package com.newbiechen.nbreader.ui.component.book.text.config

import android.graphics.drawable.Drawable

/**
 *  author : newbiechen
 *  date : 2020/3/11 6:38 PM
 *  description :
 */

class DefaultConfigure private constructor() : TextConfigure {

    companion object {
        @Volatile
        private var instance: DefaultConfigure? = null

        fun getInstance() = instance ?: synchronized(this) {
            DefaultConfigure()
        }
    }

    override fun getMarginLeft(): Int {
        return 0
    }

    override fun getMarginRight(): Int {
        return 0
    }

    override fun getMarginTop(): Int {
        return 0
    }

    override fun getMarginBottom(): Int {
        return 0
    }

    override fun getTextColor(): Int {
        return 0xff000000.toInt()
    }

    override fun getBackground(): Drawable? {
        return null
    }
}