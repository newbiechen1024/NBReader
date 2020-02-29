package com.newbiechen.nbreader.ui.component.book.text.entity.resource.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Size
import com.newbiechen.nbreader.ui.component.book.text.util.TextBitmapUtil
import com.newbiechen.nbreader.ui.component.book.text.util.TextConstants
import com.newbiechen.nbreader.uilts.LogHelper
import net.lingala.zip4j.ZipFile
import java.io.IOException
import kotlin.math.max

/**
 *  author : newbiechen
 *  date : 2020/2/29 4:40 PM
 *  description :zip 图片
 */

class ZipImage(private val path: String) : TextImage {
    private var mBitmapData: ByteArray? = null
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
        // 是否图片尺寸被修改了
        val isSizeChanged = if (mImageSize == null) {
            maxSize != null
        } else {
            mImageSize!! != maxSize
        }

        // 获取图片
        if (mBitmap == null || isSizeChanged) {
            val pathArr = path.split(TextConstants.zipSeparator)
            val rootPath = pathArr[0]
            val subPath = pathArr[1]

            // TODO: zip 信息是否最好也缓存？

            // 获取压缩文件
            val zipFile = ZipFile(rootPath)
            val imageHeader = zipFile.getFileHeader(subPath)

            // 获取文件流
            val imageInputStream = zipFile.getInputStream(imageHeader)

            // 计算最大像素值
            val maxNumOfPixels = if (maxSize != null) {
                maxSize.width * maxSize.height
            } else {
                -1
            }

            // TODO:验证数据流，是否能够获取图片 (可以)
            // TODO:就是说得有原图 ByteData  数据，和解压后的两张图。
            mBitmap = BitmapFactory.decodeStream(imageInputStream)

            // 当前图片使用的尺寸
            mImageSize = maxSize

/*            // 获取图片
            mBitmap = TextBitmapUtil.getImage(imageInputStream, maxNumOfPixels)*/
            imageInputStream.close()
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