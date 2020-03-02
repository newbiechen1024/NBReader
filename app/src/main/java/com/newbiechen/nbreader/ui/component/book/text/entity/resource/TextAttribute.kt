package com.newbiechen.nbreader.ui.component.book.text.entity.resource

import com.newbiechen.nbreader.ui.component.book.text.parcel.TextParcel

/**
 *  author : newbiechen
 *  date : 2020/3/2 5:24 PM
 *  description :
 */

// 资源属性标记
interface TextAttribute

// 图片资源属性
data class TextImageAttr(
    val id: String,
    val path: String
) : TextAttribute {
    constructor(parcel: TextParcel) : this(
        parcel.readString16(),
        parcel.readString16()
    )

    override fun toString(): String {
        return "TextImageAttr(id='$id', path='$path')"
    }

}