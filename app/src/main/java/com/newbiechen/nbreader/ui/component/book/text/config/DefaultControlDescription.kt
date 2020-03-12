package com.newbiechen.nbreader.ui.component.book.text.config

import android.content.Context
import com.newbiechen.nbreader.ui.component.book.text.entity.textstyle.ControlStyleDescription
import com.newbiechen.nbreader.ui.component.book.text.util.TextCSSReader

/**
 *  author : newbiechen
 *  date : 2020/3/12 1:08 AM
 *  description :默认的控制描述文件
 *
 *  TODO:暂时这么使用，之后支持自定义
 */

class DefaultControlDescription {
    companion object {
        @Volatile
        private var instance: DefaultControlDescription? = null

        fun getInstance() = instance ?: synchronized(this) {
            DefaultControlDescription()
        }
    }

    private var mDescriptionMap: Map<Int, ControlStyleDescription>? = null

    fun getControlDescription(context: Context): Map<Int, ControlStyleDescription> {
        if (mDescriptionMap == null) {
            val inputStream = context.assets.open("default/styles.css")
            mDescriptionMap = TextCSSReader().read(inputStream)
            inputStream.close()
        }
        return mDescriptionMap!!
    }
}