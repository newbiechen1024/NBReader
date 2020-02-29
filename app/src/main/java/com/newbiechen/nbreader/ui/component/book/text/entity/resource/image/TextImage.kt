package com.newbiechen.nbreader.ui.component.book.text.entity.resource.image

import android.graphics.Bitmap
import android.util.Size

/**
 *  author : newbiechen
 *  date : 2020/2/29 4:37 PM
 *  description :文本图片接口
 */

// TODO:图片来源 zip、file、network
// TODO:如何做图片缓存的问题
// TODO:支持设置占位图片
interface TextImage {
    /**
     * 图片源路径
     */
    fun getPath(): String

    /**
     * 图片的最大尺寸
     */
    fun getImage(maxSize: Size?): Bitmap?

    /**
     * 请求图片的大小
     */
    fun requestImageSize(maxSize: Size?): Size?
}