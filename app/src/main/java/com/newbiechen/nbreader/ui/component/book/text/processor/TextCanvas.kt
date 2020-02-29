package com.newbiechen.nbreader.ui.component.book.text.processor

import android.graphics.*
import android.util.Size
import com.newbiechen.nbreader.ui.component.book.text.entity.resource.image.TextImage

/**
 *  author : newbiechen
 *  date : 2019-10-24 16:42
 *  description :文本绘制画布
 */

class TextCanvas(private val canvas: Canvas) {

    companion object {
        private const val TAG = "TextCanvas"
    }

    /**
     * 绘制颜色
     */
    fun drawColor(color: Int, mode: PorterDuff.Mode? = null) {
        if (mode != null) {
            canvas.drawColor(color, mode)
        } else {
            canvas.drawColor(color)
        }
    }

    /**
     * 绘制线条
     */
    fun drawLine(x0: Int, y0: Int, x1: Int, y1: Int, paintContext: TextPaintContext) {
        val paint = paintContext.linePaint
        paint.isAntiAlias = false
        canvas.drawLine(x0.toFloat(), y0.toFloat(), x1.toFloat(), y1.toFloat(), paint)
        canvas.drawPoint(x0.toFloat(), y0.toFloat(), paint)
        canvas.drawPoint(x1.toFloat(), y1.toFloat(), paint)
        paint.isAntiAlias = true
    }

    /**
     * 绘制文本
     */
    fun drawString(
        x: Int,
        y: Int,
        string: CharArray,
        offset: Int,
        length: Int,
        paintContext: TextPaintContext
    ) {
        var containsSoftHyphen = false
        for (i in offset until offset + length) {
            if (string[i] == 0xAD.toChar()) {
                containsSoftHyphen = true
                break
            }
        }

        if (!containsSoftHyphen) {
            canvas.drawText(
                string,
                offset,
                length,
                x.toFloat(),
                y.toFloat(),
                paintContext.textPaint
            )
        } else {
            val corrected = CharArray(length)
            var len = 0
            for (o in offset until offset + length) {
                val chr = string[o]
                if (chr != 0xAD.toChar()) {
                    corrected[len++] = chr
                }
            }
            canvas.drawText(corrected, 0, len, x.toFloat(), y.toFloat(), paintContext.textPaint)
        }
    }

    fun drawOutline(xs: IntArray, ys: IntArray, paintContext: TextPaintContext) {
        val last = xs.size - 1
        var xStart = (xs[0] + xs[last]) / 2
        var yStart = (ys[0] + ys[last]) / 2
        var xEnd = xStart
        var yEnd = yStart
        if (xs[0] != xs[last]) {
            if (xs[0] > xs[last]) {
                xStart -= 5
                xEnd += 5
            } else {
                xStart += 5
                xEnd -= 5
            }
        } else {
            if (ys[0] > ys[last]) {
                yStart -= 5
                yEnd += 5
            } else {
                yStart += 5
                yEnd -= 5
            }
        }

        val path = Path()
        path.moveTo(xStart.toFloat(), yStart.toFloat())
        for (i in 0..last) {
            path.lineTo(xs[i].toFloat(), ys[i].toFloat())
        }
        path.lineTo(xEnd.toFloat(), yEnd.toFloat())
        canvas.drawPath(path, paintContext.outlinePaint)
    }

    /**
     * 绘制多边形线条
     */
    // 使用 path + linePaint
    fun drawPolygonalLine(xs: IntArray, ys: IntArray, paintContext: TextPaintContext) {
        val path = Path()
        val last = xs.size - 1
        path.moveTo(xs[last].toFloat(), ys[last].toFloat())
        for (i in 0..last) {
            path.lineTo(xs[i].toFloat(), ys[i].toFloat())
        }
        canvas.drawPath(path, paintContext.linePaint)
    }

    /**
     * 绘制图片
     */
    fun drawImage(
        x: Int,
        y: Int,
        image: TextImage,
        maxSize: Size,
/*        scaling: ScalingType?,
        adjustingMode: ColorAdjustingMode?*/
        paintContext: TextPaintContext
    ) {
        // TODO:暂时不处理压缩和缩放
        // 从 ZLImageData 中获取图片
        val bitmap: Bitmap = image.getBitmap()!!

        // 设置图片与背景的混合模式
        if (bitmap != null && !bitmap.isRecycled) {
            // TODO:暂时不处理混合模式
/*            when (adjustingMode) {
                LIGHTEN_TO_BACKGROUND -> myFillPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.LIGHTEN))
                DARKEN_TO_BACKGROUND -> myFillPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.DARKEN))
                NONE -> {
                }
            }*/
            // 直接绘制图片
            canvas.drawBitmap(
                bitmap,
                x.toFloat(),
                y - bitmap.height.toFloat(),
                paintContext.fillPaint
            )
            paintContext.fillPaint.xfermode = null
        }
    }

    /**
     * 填充矩形
     */
    fun fillRectangle(x0: Int, y0: Int, x1: Int, y1: Int, paintContext: TextPaintContext) {
        var x0 = x0
        var y0 = y0
        var x1 = x1
        var y1 = y1

        if (x1 < x0) {
            val swap = x1
            x1 = x0
            x0 = swap
        }
        if (y1 < y0) {
            val swap = y1
            y1 = y0
            y0 = swap
        }

        canvas.drawRect(
            x0.toFloat(),
            y0.toFloat(),
            (x1 + 1).toFloat(),
            (y1 + 1).toFloat(),
            paintContext.fillPaint
        )
    }

    fun fillCircle(x: Int, y: Int, radius: Int, paintContext: TextPaintContext) {
        canvas.drawCircle(x.toFloat(), y.toFloat(), radius.toFloat(), paintContext.fillPaint)
    }

    fun fillPolygon(xs: IntArray, ys: IntArray, paintContext: TextPaintContext) {
        val path = Path()
        val last = xs.size - 1
        path.moveTo(xs[last].toFloat(), ys[last].toFloat())
        for (i in 0..last) {
            path.lineTo(xs[i].toFloat(), ys[i].toFloat())
        }
        canvas.drawPath(path, paintContext.fillPaint)
    }
}