package com.example.newbiechen.nbreader.ui.component.book.plugin

import android.content.Context
import com.example.newbiechen.nbreader.ui.component.book.type.BookType
import com.example.newbiechen.nbreader.uilts.LogHelper

/**
 *  author : newbiechen
 *  date : 2019-09-19 16:56
 *  description :文本插件管理类
 */

class FormatPluginManager {

    companion object {
        @Volatile
        private var instance: FormatPluginManager? = null
        private const val TAG = "FormatPluginManager"
        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: FormatPluginManager(context.applicationContext).also { instance = it }
            }
    }

    private var mNativePluginList: MutableList<NativeFormatPlugin> = mutableListOf()

    private constructor(context: Context) {
        getPluginTypes().forEach {
            LogHelper.i(TAG, "pluginTypes: $it")
            // 根据 type 创建插件
            val plugin: NativeFormatPlugin = when (it.toUpperCase()) {
                BookType.TXT.toString() -> NativeFormatPlugin(context, BookType.TXT)
                BookType.EPUB.toString() -> OEBNativePlugin(context, BookType.EPUB)
                else -> NativeFormatPlugin(context, BookType.TXT)
            }

            // 存储到列表中
            mNativePluginList.add(plugin)
        }
    }

    fun getPlugin(type: BookType): FormatPlugin? {
        return mNativePluginList.firstOrNull {
            it.getSupportType() == type
        }
    }

    /**
     * 获取并初始化 native 层的 plugin
     *
     * @return
     */
    private external fun getPluginTypes(): Array<String>

    /**
     * 释放 native 层插件
     */
    private external fun freePlugins()

    protected fun finalize() {
        freePlugins()
    }
}