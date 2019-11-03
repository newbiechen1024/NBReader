package com.example.newbiechen.nbreader.ui.component.book.text.processor

import android.graphics.CornerPathEffect
import android.graphics.EmbossMaskFilter
import android.graphics.Paint
import android.graphics.Rect
import java.util.*

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

    fun initPaint() {
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
        size: Int,
        bold: Boolean,
        italic: Boolean,
        underline: Boolean,
        strikeThrough: Boolean
    ) {
        // TODO:字体暂时先不处理
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


    private val mCharHeights = TreeMap<Char, Int>()

    // TODO:这个东西的缓存是否会造成问题？？？，FBReader 每次都会重新创建一次 Paint，这个等逻辑完成后再看

    // 获取单词的高度
    fun getCharHeight(ch: Char): Int {
        val h = mCharHeights[ch]
        if (h != null) {
            return h!!
        }

        val r = Rect()
        val txt = charArrayOf(ch)
        // 通过 text paint 计算 char 的高度
        textPaint.getTextBounds(txt, 0, 1, r)
        val he = r.bottom - r.top
        mCharHeights[ch] = he

        return he
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