package com.example.newbiechen.nbreader.ui.component.book.text.config

import android.content.Context
import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.ui.component.book.text.entity.textstyle.TextDecoratedStyleDescription
import com.example.newbiechen.nbreader.ui.component.book.text.util.TextCSSReader

/**
 *  author : newbiechen
 *  date : 2019-10-24 16:02
 *  description :TextProcessor 处理器的配置参数信息
 */

class TextConfig private constructor(context: Context) {

    companion object {
        private var sInstance: TextConfig? = null

        fun getInstance(context: Context): TextConfig {
            if (sInstance == null) {
                sInstance = TextConfig(context.applicationContext)
            }
            return sInstance!!
        }
    }

    private val mSharedPrefs = TextStyleSharedPrefs.getInstance(context)

    // 将 index 作为 type 的 key
    // 最大长度为 256 的因为，fbreader 的 type id 是 unsigned char 类型 。
    private val mDescriptionMap = arrayOfNulls<TextDecoratedStyleDescription>(256)

    init {
        // 获取 asset 资源
        val resourceInputStream = context.assets.open("default/style.css")
        val descriptionMap = TextCSSReader()
            .read(resourceInputStream)

        // 将获取的样式信息 map 转换成数组 array
        descriptionMap.forEach { entry ->
            mDescriptionMap[entry.key and 0xFF] = entry.value
        }
    }

    val defaultTextStyle = DefaultTextStyle.getInstance(context)

    val leftMargin = 0
    val topMargin = 0
    val rightMargin = 0
    val bottomMargin = 0

    val getWallpaperPath = mSharedPrefs.wallpaperPath

    var bgColor: Int
        set(value) {
            mSharedPrefs.bgColor = value
        }
        get() = mSharedPrefs.bgColor

    var textColor:Int
        set(value) {
            mSharedPrefs.textColor = value
        }
        get() = mSharedPrefs.textColor

    fun getTextDecoratedStyleDesc(styleType: Byte): TextDecoratedStyleDescription? {
        // 解决 byte 转 int 导致的问题
        return mDescriptionMap[styleType.toInt() and 0xFF]
    }
}