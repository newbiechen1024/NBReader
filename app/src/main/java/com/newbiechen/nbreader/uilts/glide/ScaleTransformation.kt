package com.newbiechen.nbreader.uilts.glide

import android.content.Context
import android.graphics.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import jp.wasabeef.glide.transformations.BitmapTransformation
import java.security.MessageDigest

/**
 *  author : newbiechen
 *  date : 2019-08-16 14:05
 *  description : 图片大小缩放
 */

class ScaleTransformation(private val scale: Float) : BitmapTransformation() {

    companion object {
        private const val ID = "com.newbiechen.nbreader.ScaleTransformation"
    }

    override fun transform(
        context: Context,
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        // 从缓冲区获取一个 bitmap 缓冲区
        val bitmap = pool.get(toTransform.width, toTransform.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 放大并绘制图片
        val paint = Paint().apply {
            flags = Paint.FILTER_BITMAP_FLAG
        }

        canvas.scale(scale, scale, toTransform.width / 2.0f, toTransform.height / 2.0f)
        canvas.drawBitmap(toTransform, 0f, 0f, paint)
        return bitmap
    }

    override fun hashCode(): Int {
        return ID.hashCode() + (scale * 1000).toInt()
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update((ID + scale).toByteArray(CHARSET))
    }

    override fun equals(other: Any?): Boolean {
        return other is ScaleTransformation && other.scale == scale
    }
}

