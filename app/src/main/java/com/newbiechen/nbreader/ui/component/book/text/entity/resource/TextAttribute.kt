package com.newbiechen.nbreader.ui.component.book.text.entity.resource

/**
 *  author : newbiechen
 *  date : 2020/2/29 2:37 PM
 *  description :文本属性
 */

interface TextAttribute

// 文本图片属性
data class TextImageAttr(
    // 图片 id
    val id: Int,
    // 图片路径
    val path: String
) : TextAttribute {

    override fun toString(): String {
        return "TextImageAttr(id=$id, path='$path')"
    }
}

