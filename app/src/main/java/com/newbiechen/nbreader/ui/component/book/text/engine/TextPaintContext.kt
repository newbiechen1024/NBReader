package com.newbiechen.nbreader.ui.component.book.text.engine

import android.content.Context
import android.graphics.CornerPathEffect
import android.graphics.EmbossMaskFilter
import android.graphics.Paint
import android.util.Size
import com.newbiechen.nbreader.ui.component.book.text.entity.resource.image.TextImage
import com.newbiechen.nbreader.uilts.DensityUtil

/**
 *  author : newbiechen
 *  date : 2019-10-24 16:42
 *  description :文本画笔上下文
 */

class TextPaintContext {
    val textPaint: Paint = Paint()
    val linePaint: Paint = Paint()
    val fillPaint: Paint = Paint()
    val outlinePaint: Paint = Paint()

    init {
        initPaint()
    }

    private fun initPaint() {
        textPaint.apply {
            isLinearText = true
            isAntiAlias = true
            isDither = true
            isSubpixelText = true
        }

        linePaint.apply {
            style = Paint.Style.STROKE
            isAntiAlias = true
            isDither = true
        }

        outlinePaint.apply {
            isAntiAlias = true
            isDither = true
            strokeWidth = 4F
            style = Paint.Style.STROKE
            pathEffect = CornerPathEffect(5F)
            maskFilter = EmbossMaskFilter(floatArrayOf(1f, 1f, 1f), .4f, 6f, 3.5f)
        }
    }

    // 设置字体
    fun setFont(
        context: Context,
        size: Int,
        bold: Boolean,
        italic: Boolean,
        underline: Boolean,
        strikeThrough: Boolean
    ) {
        // 设置 TextPaint

        // TODO：传入字符集的名字，调用 FontManager 获取对应的 typeface 字符库。

        textPaint.textSize = DensityUtil.sp2px(context, size.toFloat()).toFloat()
        textPaint.isUnderlineText = underline
        textPaint.isStrikeThruText = strikeThrough
    }

    /**
     * 设置字体颜色
     */
    fun setTextColor(color: Int) {
        textPaint.color = color
    }

    /**
     * 设置线条颜色
     */
    fun setLineColor(color: Int) {
        linePaint.color = color
        outlinePaint.color = color
    }

    /**
     * 设置线条的粗细
     */
    fun setLineThickness(thickness: Float) {
        linePaint.strokeWidth = thickness
    }

    /**
     * 设置填充颜色
     */
    fun setFillColor(color: Int) {
        fillPaint.color = color
    }

    /**
     * 获取文本的宽度
     */
    fun getStringWidth(string: CharArray, offset: Int, length: Int): Int {
        var containsSoftHyphen = false

        // 检测是否有软连字符，0xAD 是软连字符的字节码
        // https://bianjp.com/posts/2017/12/16/soft-hyphen
        // 作用：软连字符表示建议换行的意思
        for (i in offset until offset + length) {
            if (string[i] == 0xAD.toChar()) {
                containsSoftHyphen = true
                break
            }
        }

        // 如果不包含软连字符，则直接使用 textPaint 计算文本的宽度
        return if (!containsSoftHyphen) {
            (textPaint.measureText(String(string, offset, length)) + 0.5f).toInt()
        } else {
            // 计算软连字符前的文本上
            val corrected = CharArray(length)
            var len = 0
            for (o in offset until offset + length) {
                val chr = string[o]
                if (chr != 0xAD.toChar()) {
                    corrected[len++] = chr
                }
            }
            (textPaint.measureText(corrected, 0, len) + 0.5f).toInt()
        }
    }

    private var mSpaceWidth: Int? = null

    fun getSpaceWidth(): Int {
        if (mSpaceWidth == null) {
            mSpaceWidth = (textPaint.measureText(" ", 0, 1) + 0.5f).toInt()
        }
        return mSpaceWidth!!
    }

    private var mStringHeight: Int? = null

    /**
     * 获取文本的高度
     */
    fun getStringHeight(): Int {
        if (mStringHeight == null) {
            mStringHeight = (textPaint.textSize + 0.5f).toInt()
        }
        return mStringHeight!!
    }

    /**
     * 计算图片的尺寸
     */
    fun getImageSize(image: TextImage, maxSize: Size/*, scaling: ScalingType?*/): Size? {
        // TODO：支持对图片的缩放，暂时不处理
        return image.requestImageSize(maxSize)
    }

    private var mDescent = -1

    fun getDescent(): Int {
        var descent = mDescent
        if (descent == -1) {
            descent = (textPaint.descent() + 0.5f).toInt()
            mDescent = descent
        }
        return descent
    }
}