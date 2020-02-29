package com.newbiechen.nbreader.ui.component.book.text.entity.resource.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import net.lingala.zip4j.ZipFile

/**
 *  author : newbiechen
 *  date : 2020/2/29 4:40 PM
 *  description :zip 图片
 */

class ZipImage(private val path: String) : TextImage {
    private var mBitmap: Bitmap? = null
    override fun getPath(): String {
        return path
    }

    // TODO:未进行错误处理
    override fun getBitmap(): Bitmap? {
        // 获取图片
        if (mBitmap == null) {
            val pathArr = path.split(":")
            val rootPath = pathArr[0]
            val subPath = pathArr[1]
            // 获取压缩文件
            val zipFile = ZipFile(rootPath)
            val imageHeader = zipFile.getFileHeader(subPath)
            // 获取文件流
            val imageInputStream = zipFile.getInputStream(imageHeader)
            // 获取图片
            mBitmap = BitmapFactory.decodeStream(imageInputStream)
        }
        return mBitmap!!
    }
}