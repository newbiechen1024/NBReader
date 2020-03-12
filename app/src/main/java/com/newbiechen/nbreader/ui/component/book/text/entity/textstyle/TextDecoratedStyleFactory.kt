package com.newbiechen.nbreader.ui.component.book.text.entity.textstyle

import com.newbiechen.nbreader.ui.component.book.text.config.CSSStyleInterceptor
import com.newbiechen.nbreader.ui.component.book.text.config.ControlStyleInterceptor
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextStyleTag

/**
 *  author : newbiechen
 *  date : 2020/3/10 9:43 PM
 *  description :文本装饰样式工厂
 */

class TextDecoratedStyleFactory(
    // 默认样式
    private val controlStyleDescMap: Map<Int, ControlStyleDescription>,
    // 控制样式拦截器
    private val controlStyleInterceptor: ControlStyleInterceptor?,
    // CSS 样式拦截器
    private val cssStyleInterceptor: CSSStyleInterceptor?
) {
    fun getControlDecoratedStyle(parent: TreeTextStyle, textKind: Byte): TreeTextStyle {

        return ControlDecoratedStyle(
            parent,
            getControlTextStyle(parent, textKind),
            getControlInterceptorStyle(textKind)
        )
    }

    fun getCSSDecoratedStyle(parent: TreeTextStyle, cssStyle: TextStyleTag): TreeTextStyle {
        return CSSDecoratedStyle(parent, cssStyle, cssStyleInterceptor)
    }

    private fun getControlInterceptorStyle(textKind: Byte): ControlInterceptorStyle? {
        return if (controlStyleInterceptor != null) {
            ControlInterceptorStyle(textKind, controlStyleInterceptor)
        } else {
            null
        }
    }

    private fun getControlTextStyle(parent: TreeTextStyle, textKind: Byte): ControlTextStyle? {
        val desc = controlStyleDescMap[textKind]
        return if (desc != null) {
            ControlTextStyle(parent, desc)
        } else {
            null
        }
    }
}