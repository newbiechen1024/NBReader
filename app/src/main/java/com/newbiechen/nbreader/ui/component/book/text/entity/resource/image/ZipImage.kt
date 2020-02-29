package com.newbiechen.nbreader.ui.component.book.text.entity.resource.image

import android.graphics.Bitmap
import android.util.Size
import com.newbiechen.nbreader.ui.component.book.text.util.TextBitmapUtil
import com.newbiechen.nbreader.ui.component.book.text.util.TextConstants
import net.lingala.zip4j.ZipFile
import java.io.ByteArrayInputStream

/**
 *  author : newbiechen
 *  date : 2020/2/29 4:40 PM
 *  description :zip 图片
 */

class ZipImage(private val path: String) : TextImage {
    private var mBitmapBuffer: ByteArray? = null
    private var mBitmap: Bitmap? = null
    private var mImageSize: Size? = null

    companion object {
        private const val TAG = "ZipImage"
    }

    override fun getPath(): String {
        return path
    }

    // TODO:未进行错误处理
    override fun getImage(maxSize: Size?): Bitmap? {
        // 缓存原图信息
        if (mBitmapBuffer == null) {
            val pathArr = path.split(TextConstants.zipSeparator)
            val rootPath = pathArr[0]
            val subPath = pathArr[1]
            // 获取压缩文件
            val zipFile = ZipFile(rootPath)
            val imageHeader = zipFile.getFileHeader(subPath)
            // 获取文件流
            val imageInputStream = zipFile.getInputStream(imageHeader)

            // 创建缓冲区
            mBitmapBuffer = ByteArray(imageHeader.uncompressedSize.toInt())

            var bufferOffset = 0

            // zip 的读取流和普通的流不一样，所以需要用这种方式读取图片数据
            do {
                bufferOffset += imageInputStream.read(
                    mBitmapBuffer,
                    bufferOffset,
                    mBitmapBuffer!!.size - bufferOffset
                )
            } while (bufferOffset != 0 && bufferOffset < mBitmapBuffer!!.size)

            // 关闭数据流
            imageInputStream.close()
        }

        // 是否图片尺寸被修改了
        val isSizeChanged = if (mImageSize == null) {
            maxSize != null
        } else {
            mImageSize!! != maxSize
        }


        // 获取图片
        if (mBitmap == null || isSizeChanged) {
            // 计算最大像素值
            val maxNumOfPixels = if (maxSize != null) {
                maxSize.width * maxSize.height
            } else {
                -1
            }

            val imageIs = ByteArrayInputStream(mBitmapBuffer)

            // 获取图片
            mBitmap = TextBitmapUtil.getImage(imageIs, maxNumOfPixels)

            // 当前图片使用的尺寸
            mImageSize = maxSize

            // 关闭数据流
            imageIs.close()
        }

        return mBitmap!!
    }

    override fun requestImageSize(maxSize: Size?): Size? {
        val bitmap = getImage(maxSize)
        return if (bitmap != null) {
            Size(bitmap.width, bitmap.height)
        } else {
            null
        }
    }
}