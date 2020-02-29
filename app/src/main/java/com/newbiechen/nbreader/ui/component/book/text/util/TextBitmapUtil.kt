package com.newbiechen.nbreader.ui.component.book.text.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Size
import com.newbiechen.nbreader.uilts.LogHelper
import java.io.InputStream
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

/**
 *  author : newbiechen
 *  date : 2020/2/29 8:51 PM
 *  description :图片处理工具
 */

object TextBitmapUtil {
    private const val TAG = "TextBitmapUtil"
    /**
     * 获取指定资源经过压缩的源图bitmap
     *
     * @param maxNumOfPixels 返回的结果bitmap的最大像素数, -1时忽略
     * @param maxSize       返回的结果bitmap的宽/高最大值, -1时忽略
     */
    fun getImage(
        stream: InputStream,
        maxNumOfPixels: Int = -1,
        maxSize: Size = Size(-1, -1)
    ): Bitmap? {
        var maxWidth = maxSize.width
        var maxHeight = maxSize.height
        val options = BitmapFactory.Options()
        val imageSize: Size = getImageSize(stream)

        LogHelper.i(TAG, "getImage: size = $imageSize")
        // 获取缩放比
        options.inSampleSize = computeSampleSize(imageSize, -1, maxNumOfPixels)

        var bitmap: Bitmap = BitmapFactory.decodeStream(stream, null, options) ?: return null

        LogHelper.i(TAG, "getImage: bitmap success")

        // 处理最大宽高
        if (maxWidth > 0 || maxHeight > 0) {

            maxWidth = if (maxWidth > 0) maxWidth else imageSize.width
            maxHeight = if (maxHeight > 0) maxHeight else imageSize.height

            // 根据最大宽高计算最小压缩比
            val minScale = ceil(
                (imageSize.width.toDouble() / maxWidth).coerceAtLeast(imageSize.height.toDouble() / maxHeight)
            ).toFloat()

            // 如果 inSampleSize 没有达到最小压缩比则需要对图片再次进行缩放
            if (minScale > 1 && minScale > options.inSampleSize) {
                val scale = (maxWidth.toFloat() / bitmap.width)
                    .coerceAtMost(maxHeight.toFloat() / bitmap.height)

                val matrix = Matrix()
                matrix.postScale(scale, scale)
                bitmap = Bitmap.createBitmap(
                    bitmap, 0, 0,
                    bitmap.width,
                    bitmap.height,
                    matrix, false
                )
            }
        }

        return bitmap
    }

    /**
     * 获取指定流的图片大小
     */
    fun getImageSize(stream: InputStream): Size {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ALPHA_8
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(stream, null, options)
        // 跳回到起始位置
        stream.skip(0)
        stream.reset()
        return Size(options.outWidth, options.outHeight)
    }

    /**
     * 计算inSampleSize用于压缩大图
     * @param imageSize：图片宽高
     * @param maxNumOfPixels:最大像素
     * @param minSideLength：最小边长
     */
    fun computeSampleSize(imageSize: Size, minSideLength: Int, maxNumOfPixels: Int): Int {

        val initialSize = computeInitialSampleSize(
            imageSize, minSideLength,
            maxNumOfPixels
        )

        var roundedSize: Int

        if (initialSize <= 8) {
            roundedSize = 1
            while (roundedSize < initialSize) {
                roundedSize = roundedSize shl 1
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8
        }

        return roundedSize
    }

    /**
     * @param imageSize：图片宽高
     * @param maxNumOfPixels:最大像素
     * @param minSideLength：最小边长
     */
    private fun computeInitialSampleSize(
        imageSize: Size,
        minSideLength: Int, maxNumOfPixels: Int
    ): Int {
        val w: Int = imageSize.width
        val h: Int = imageSize.height

        val lowerBound =
            if (maxNumOfPixels == -1) 1 else ceil(sqrt((w * h / maxNumOfPixels).toDouble())).toInt()

        val upperBound = if (minSideLength == -1) 128 else floor((w / minSideLength).toDouble())
            .coerceAtMost(floor((h / minSideLength).toDouble())).toInt()

        if (upperBound < lowerBound) {
            return lowerBound
        }

        return if (maxNumOfPixels == -1 && minSideLength == -1) {
            1
        } else if (minSideLength == -1) {
            lowerBound
        } else {
            upperBound
        }
    }
}