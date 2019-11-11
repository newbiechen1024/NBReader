package com.example.newbiechen.nbreader.ui.component.book.text.config

import android.content.Context
import android.content.SharedPreferences
import com.example.newbiechen.nbreader.uilts.NBSharedPrefs

/**
 *  author : newbiechen
 *  date : 2019-11-10 16:31
 *  description :文本样式配置信息
 */

class TextStyleSharedPrefs private constructor(private val context: Context) : NBSharedPrefs() {

    companion object {
        private const val SP_SHARE_NAME = "sp_text_style"

        // 背景颜色
        private const val SP_BG_COLOR = "sp_bg_color"
        // 壁纸颜色
        private const val SP_WALLPAPER_PATH = "sp_wallpapaer_path"

        // 主题样式
        private const val SP_THEME = "sp_theme"

        // 文字颜色
        private const val SP_TEXT_COLOR = "sp_text_color"

        // 文字大小
        private const val SP_TEXT_SIZE = "sp_text_size"

        private var sInstance: TextStyleSharedPrefs? = null

        fun getInstance(context: Context): TextStyleSharedPrefs {
            if (sInstance == null) {
                sInstance =
                    TextStyleSharedPrefs(
                        context.applicationContext
                    )
            }
            return sInstance!!
        }
    }

    override fun initSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(SP_SHARE_NAME, Context.MODE_PRIVATE)
    }

    var textColor: Int
        set(value) {
            putInt(SP_TEXT_COLOR, value)
        }
        get() {
            return getInt(SP_TEXT_COLOR, 0XFF000000.toInt())
        }

    var textSize: Int
        set(value) {
            putInt(SP_TEXT_SIZE, value)
        }
        get() {
            return getInt(SP_TEXT_SIZE, 18)
        }

    var bgColor: Int
        set(value) {
            putInt(SP_BG_COLOR, value)
        }
        get() {
            return getInt(SP_BG_COLOR, 0XFFCEC29C.toInt())
        }

    var wallpaperPath: String
        set(value) {
            putString(SP_WALLPAPER_PATH, value)
        }
        get() {
            return getString(SP_WALLPAPER_PATH, "")!!
        }
}