package com.example.newbiechen.nbreader.ui.component.book.text.util

import android.content.Context
import com.example.newbiechen.nbreader.ui.component.book.text.entity.textstyle.CustomTextDecoratedStyle
import com.example.newbiechen.nbreader.ui.component.book.text.entity.textstyle.DefaultTextStyle
import com.example.newbiechen.nbreader.ui.component.book.text.entity.textstyle.TextDecoratedStyleDescription
import com.example.newbiechen.nbreader.ui.component.book.text.entity.textstyle.TextStyle

/**
 *  author : newbiechen
 *  date : 2019-10-24 16:02
 *  description :TextProcessor 处理器的配置参数信息
 */

class TextConfig {

    // TODO:实现 SharedPreference 并实现一个 Builder 进行构建。
    // 需要传入 context 获取配置项。。

    fun getTextDecoratedStyleDesc(styleType: Byte): TextDecoratedStyleDescription {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getDefaultTextStyle(): DefaultTextStyle {
        return DefaultTextStyle()
    }

    fun getLeftMargin(): Int {
        return 0
    }

    fun getRightMargin(): Int {
        return 0
    }

    fun getTopMargin(): Int {
        return 0
    }

    fun getBottomMargin(): Int {
        return 0
    }

    fun getWallpaperFile(): String? {
        return null
    }

    fun getBackgroundColor(): Int {
        // TODO:暂时先这样
        return 0XFF000000.toInt()
    }

    /**
     * TODO：暂时不考虑超链接的问题
     */
    fun getTextColor(): Int {
        return 0XFFFFFFFF.toInt()
    }

    // 获取 TextStyle 集合
}