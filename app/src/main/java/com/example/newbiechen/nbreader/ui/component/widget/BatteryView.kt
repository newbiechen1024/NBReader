package com.example.newbiechen.nbreader.ui.component.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.FloatRange
import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.uilts.DensityUtil

/**
 *  author : newbiechen
 *  date : 2020-01-28 14:18
 *  description :电池 View
 *
 *  TODO:之后添加 typeAttr
 */

class BatteryView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 外框制作
    private lateinit var mOutlinePaint: Paint
    // 填充内容制作
    private lateinit var mFillPaint: Paint

    companion object {
        // 电极的高度与边框高度的比例
        private const val POLAR_HEIGHT_RATIO = 1.0f / 2
        // 电极的宽度与电极的高度的比例
        private const val POLAR_WIDTH_RATIO = 1.0f / 3
    }

    // 默认颜色
    private val defaultOutlineColor = resources.getColor(R.color.colorBlack)
    private val defaultFillColor = defaultOutlineColor

    // 电池的进度
    private var mProgress: Float = 1.0f
    // 边框厚度
    private var mBorderWidth: Int = DensityUtil.dp2px(context, 1.0f)
    // 边框内部
    private var mBorderPadding: Int = 1

    init {
        initPaint()
    }

    private fun initPaint() {
        // 绘制电池外框
        mOutlinePaint = Paint().apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeWidth = mBorderWidth.toFloat()
            color = defaultOutlineColor
        }

        // 绘制电池填充
        mFillPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
            color = defaultFillColor
            style = Paint.Style.FILL
        }
    }

    /**
     * 设置外轮廓颜色
     */
    fun setOutlineColor(color: Int) {
        if (mOutlinePaint.color == color) {
            return
        }

        mOutlinePaint.color = color
        invalidate()
    }

    /**
     * 设置填充颜色
     */
    fun setFillColor(color: Int) {
        if (mOutlinePaint.color == color) {
            return
        }

        mFillPaint.color = color
        invalidate()
    }

    /**
     * 设置边框大小
     */
    fun setBorderWidth(width: Int) {
        if (mBorderWidth == width) {
            return
        }

        mBorderWidth = width
        invalidate()
    }

    fun setBorderPadding(padding: Int) {
        if (mBorderPadding == padding) {
            return
        }

        mBorderPadding = padding
        invalidate()
    }

    /**
     * 设置电池进度
     */
    fun setProgress(@FloatRange(from = 0.0, to = 1.0) progress: Float) {
        if (mProgress == progress) {
            return
        }

        mProgress = progress
        invalidate()
    }

    private var mPolarRect = RectF()
    private var mOutlineRect = RectF()
    private var mFillRect = RectF()

    override fun onDraw(canvas: Canvas?) {
        // 电池的电极
        val polarHeight: Float = height * POLAR_HEIGHT_RATIO
        val polarWidth: Float = polarHeight * POLAR_WIDTH_RATIO

        //电极的制作
        val polarLeft = width - polarWidth
        val polarTop = (height - polarHeight) / 2

        mPolarRect.set(
            polarLeft, polarTop,
            width.toFloat(), polarTop + polarHeight
        )

        canvas!!.drawRect(mPolarRect, mFillPaint)

        // strokeWidth 是以 rect 位置为中心位置绘制的，所以需要除以 2，才能表示笔触导致的内部收缩的问题
        val borderSpace = mBorderWidth.toFloat() / 2

        // 外框的制作
        mOutlineRect.set(
            borderSpace,
            borderSpace,
            polarLeft - borderSpace,
            height.toFloat() - borderSpace
        )

        canvas!!.drawRect(mOutlineRect, mOutlinePaint)

        // 内部间距
        val innerPadding = mBorderWidth + borderSpace

        // 填充进度的制作
        val fillWidth: Float =
            (mOutlineRect.width() - innerPadding * 2) * mProgress

        mFillRect.set(
            (mOutlineRect.left + innerPadding),
            (mOutlineRect.top + innerPadding),
            (mOutlineRect.left + innerPadding + fillWidth),
            (mOutlineRect.bottom - innerPadding)
        )

        canvas.drawRect(mFillRect, mFillPaint)
    }
}