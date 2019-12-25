package com.example.newbiechen.nbreader.ui.component.book.plugin

import android.content.Context
import android.content.res.AssetManager
import com.example.newbiechen.nbreader.ui.component.book.type.BookType
import com.example.newbiechen.nbreader.uilts.LogHelper

/**
 *  author : newbiechen
 *  date : 2019-09-19 16:56
 *  description :文本插件管理类
 */

class BookPluginManager {

    companion object {
        @Volatile
        private var instance: BookPluginManager? = null
        private const val TAG = "BookPluginManager"
        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: BookPluginManager(context.applicationContext).also { instance = it }
            }
    }

    private var mNativePluginList: MutableList<NativeFormatPlugin> = mutableListOf()

    private constructor(context: Context) {
        // 进行注册
        getSupportPluginTypes().forEach {
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

        registerAssetManager(context.assets)
    }

    fun getPlugin(type: BookType): NativeFormatPlugin? {
        return mNativePluginList.firstOrNull {
            it.getSupportType() == type
        }
    }

    private external fun registerAssetManager(assetManager: AssetManager)

    /**
     * 获取并初始化 native 层的 plugin
     *
     * @return
     */
    private external fun getSupportPluginTypes(): Array<String>
}