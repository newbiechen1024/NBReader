package com.example.newbiechen.nbreader.ui.component.book.text.processor

import com.example.newbiechen.nbreader.ui.component.book.text.entity.textstyle.CustomTextDecoratedStyle
import com.example.newbiechen.nbreader.ui.component.book.text.entity.textstyle.TextDecoratedStyleDescription
import com.example.newbiechen.nbreader.ui.component.book.text.entity.textstyle.TextStyle

/**
 *  author : newbiechen
 *  date : 2019-10-24 16:02
 *  description :TextProcessor 处理器的配置参数信息
 */

interface TextConfig {
    fun getLeftMargin(): Int
    fun getRightMargin(): Int
    fun getTopMargin(): Int
    fun getBottomMargin(): Int

    /**
     * 获取壁纸文件路径
     * @return 如果没有壁纸，则默认使用 Color
     */
    fun getWallpaperFile(): String?

    /**
     * 获取背景颜色
     */
    fun getBackgroundColor(): Int

    /**
     * 获取文本样式
     */
    fun getTextStyle(): TextStyle

    /**
     * 获取文本装饰样式
     */
    fun getTextDecoratedStyleDesc(styleType: Byte): TextDecoratedStyleDescription
}

class DefaultTextConfig : TextConfig {
    override fun getTextDecoratedStyleDesc(styleType: Byte): TextDecoratedStyleDescription {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTextStyle(): TextStyle {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLeftMargin(): Int {
        return 0
    }

    override fun getRightMargin(): Int {
        return 0
    }

    override fun getTopMargin(): Int {
        return 0
    }

    override fun getBottomMargin(): Int {
        return 0
    }

    override fun getWallpaperFile(): String? {
        return null
    }

    override fun getBackgroundColor(): Int {
        // TODO:暂时先这样
        return 0XFF000000.toInt()
    }

    // 获取 TextStyle 集合
}