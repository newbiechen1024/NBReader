package com.newbiechen.nbreader.ui.component.book.text.config

import android.content.Context
import android.graphics.drawable.Drawable
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextStyleTag
import com.newbiechen.nbreader.ui.component.book.text.entity.textstyle.*
import java.io.File

/**
 *  author : newbiechen
 *  date : 2019-10-24 16:02
 *  description :TextProcessor 处理器的配置参数信息
 */

class TextConfig private constructor(builder: Builder) {

    private val mTextConfigure: TextConfigure

    private val mTextDecoratedStyleFactory: TextDecoratedStyleFactory
    private val mDefaultTextStyle: BaseTextStyle

    init {
        mTextConfigure = builder.textConfigure
        mDefaultTextStyle = builder.defaultStyle
        mTextDecoratedStyleFactory = TextDecoratedStyleFactory(
            DefaultControlDescription.getInstance()
                .getControlDescription(builder.context), // TODO:暂时这么获取
            builder.controlInterceptor,
            builder.cssInterceptor
        )
    }

    fun getMarginLeft(): Int {
        return mTextConfigure.getMarginLeft()
    }

    fun getMarginRight(): Int {
        return mTextConfigure.getMarginRight()
    }

    fun getMarginTop(): Int {
        return mTextConfigure.getMarginTop()
    }

    fun getMarginBottom(): Int {
        return mTextConfigure.getMarginBottom()
    }

    fun getBackground(): Drawable? {
        return mTextConfigure.getBackground()
    }

    fun getTextColor(): Int {
        return mTextConfigure.getTextColor()
    }

    fun getBaseTextStyle(): BaseTextStyle {
        return mDefaultTextStyle
    }

    fun getControlDecoratedStyle(parent: TreeTextStyle, textKind: Byte): TreeTextStyle {
        return mTextDecoratedStyleFactory.getControlDecoratedStyle(parent, textKind)
    }

    fun getCSSDecoratedStyle(parent: TreeTextStyle, cssStyle: TextStyleTag): TreeTextStyle {
        return mTextDecoratedStyleFactory.getCSSDecoratedStyle(parent, cssStyle)
    }

    class Builder(internal var context: Context) {
        internal var defaultStyle: BaseTextStyle = DefaultTextStyle.getInstance()
        internal var textConfigure: TextConfigure = DefaultConfigure.getInstance()

        internal var controlCSSFile: File? = null
        internal var controlInterceptor: ControlStyleInterceptor? = null
        internal var cssInterceptor: CSSStyleInterceptor? = null

        fun configure(textConfigure: TextConfigure): Builder {
            this.textConfigure = textConfigure
            return this
        }

        // TODO：暂时不支持设置
/*        fun defaultControlStyle(file: File): Builder {
            controlCSSFile = file
            return this
        }*/

        fun defaultStyle(textStyle: BaseTextStyle): Builder {
            defaultStyle = textStyle
            return this
        }

        fun controlStyleInterceptor(interceptor: ControlStyleInterceptor): Builder {
            controlInterceptor = interceptor
            return this
        }

        fun cssStyleInterceptor(interceptor: CSSStyleInterceptor): Builder {
            cssInterceptor = interceptor
            return this
        }

        fun build(): TextConfig {
            return TextConfig(this)
        }
    }
}