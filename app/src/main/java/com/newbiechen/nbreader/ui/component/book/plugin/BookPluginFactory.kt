package com.newbiechen.nbreader.ui.component.book.plugin

import android.content.Context
import android.content.res.AssetManager
import com.newbiechen.nbreader.ui.component.book.type.BookType
import com.newbiechen.nbreader.uilts.LogHelper

/**
 *  author : newbiechen
 *  date : 2019-09-19 16:56
 *  description :文本插件管理类
 */

class BookPluginFactory {

    companion object {
        private const val TAG = "BookPluginManager"

        @Volatile
        private var instance: BookPluginFactory? = null

        private var sSupportTypes: ArrayList<BookType>? = null

        // 加载书籍处理库
        init {
            System.loadLibrary("nbbook")
        }

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                BookPluginFactory(context.applicationContext).also { instance = it }
            }
    }

    private constructor(context: Context) {
        // 原生需要注册 asset
        registerAssetManagerNative(context.assets)
    }

    fun getPlugin(type: BookType): NativeFormatPlugin {
        // 检测是否 native 支持解析该书籍类型
        check(isSupportType(type)) {
            "unsupported book type"
        }

        return when (type) {
            BookType.EPUB -> OEBNativePlugin(type)
            else -> NativeFormatPlugin(type)
        }
    }

    fun isSupportType(type: BookType): Boolean {
        return getSupportPluginTypes().contains(type)
    }

    fun getSupportPluginTypes(): List<BookType> {
        if (sSupportTypes == null) {
            val pluginTypes = getSupportPluginTypesNative()
            sSupportTypes = ArrayList(pluginTypes.size)
            pluginTypes.forEach {
                when (it.toUpperCase()) {
                    BookType.TXT.name -> {
                        sSupportTypes!!.add(BookType.TXT)
                    }
                    BookType.EPUB.name -> {
                        sSupportTypes!!.add(BookType.EPUB)
                    }
                }
            }
        }

        return sSupportTypes!!
    }

    private external fun registerAssetManagerNative(assetManager: AssetManager)

    /**
     * 获取并初始化 native 层的 plugin
     *
     * @return
     */
    private external fun getSupportPluginTypesNative(): Array<String>
}