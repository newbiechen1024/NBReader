package com.newbiechen.nbreader.ui.component.book.text.entity.resource.image

import android.graphics.Bitmap

/**
 *  author : newbiechen
 *  date : 2020/2/29 4:37 PM
 *  description :文本图片接口
 */

// TODO:图片来源 zip、file、network
// TODO:如何做图片缓存的问题
interface TextImage {
    fun getPath(): String
    fun getBitmap(): Bitmap?
}