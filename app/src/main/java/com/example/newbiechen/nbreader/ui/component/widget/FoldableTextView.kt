package com.example.newbiechen.nbreader.ui.component.widget

import android.content.Context
import android.content.res.TypedArray
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import com.example.newbiechen.nbreader.R
import kotlin.math.min

/**
 *  author : newbiechen
 *  date : 2019-08-15 14:48
 *  description: 可自定折叠后的 ellipse 文本
 */

class FoldableTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    TextView(context, attrs, defStyleAttr), View.OnClickListener {

    companion object {
        private const val TAG = "FoldableTextView"
    }

    private var mFoldEllipse: CharSequence? = null
    private var mOriginText: CharSequence? = null
    private var mClickListener: OnClickListener? = null
    private var mBufferType: BufferType? = null
    private var mFoldLines: Int
    private var isExpand: Boolean = false

    init {
        val a: TypedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.FoldableTextView, defStyleAttr, 0
        )

        mFoldEllipse = a.getString(R.styleable.FoldableTextView_foldEllipse)
        mFoldLines = a.getInteger(R.styleable.FoldableTextView_foldLines, Int.MAX_VALUE)

        a.recycle()
        super.setOnClickListener(this)
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)

        mOriginText = text
        mBufferType = type

        // 是否需要重置 text 文本内容，添加 ellipse 文本
        if (TextUtils.isEmpty(text) || maxLines <= 0 || mFoldLines == Int.MAX_VALUE) {
            return
        } else {
            formatText(text, type)
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        mClickListener = l
    }

    override fun onClick(v: View?) {
        // 如果文本相同，则表示展开状态
        isExpand = !isExpand
        // 重置文本
        text = mOriginText
        // 处理点击事件
        mClickListener?.onClick(v)
    }

    private fun formatText(text: CharSequence?, type: BufferType?) {
        if (isExpand || TextUtils.isEmpty(mFoldEllipse)) {
            return
        }

        val curText = layout?.text
        if (TextUtils.equals(curText, text)) {
            translateText(layout, text, type)
        } else {
            // 添加 layout 监听，等待 setText 处理完成
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    translateText(layout, text, type)
                }
            })
        }
    }

    private fun translateText(layout: Layout, text: CharSequence?, type: BufferType?) {
        if (layout.lineCount <= mFoldLines) {
            return
        }
        // 获取最大行数位置的 start pos
        val start = layout.getLineStart(mFoldLines - 1)
        // 获取最大行数位置的 end pos
        var end = layout.getLineVisibleEnd(mFoldLines - 1)
        // 计算 mFoldEllipse 占用该行多少字
        end -= paint.breakText(text, start, end, false, paint.measureText(mFoldEllipse.toString()), null)
        val resultText = text.toString().subSequence(0, end)

        // 判断 text 或者 foldEllipse 有一方是 Spanned
        var resultStr: CharSequence?
        if (text is Spanned || mFoldEllipse is Spanned) {
            val stringBuilder = SpannableStringBuilder()
            stringBuilder.append(resultText)
            stringBuilder.append(mFoldEllipse)
            resultStr = stringBuilder
        } else {
            val stringBuilder = StringBuilder()
            stringBuilder.append(resultText)
            stringBuilder.append(mFoldEllipse)
            resultStr = stringBuilder
        }
        // 将重置的文本，交给 text重新计算
        super.setText(resultStr, type)
    }

    /**
     * 自定义折叠后的文本
     */
    fun setFoldEllipsize(ellipse: CharSequence) {
        if (mFoldEllipse == ellipse) {
            return
        }

        mFoldEllipse = ellipse
        // 重置折叠信息
        formatText(mOriginText, mBufferType)
    }

    fun setFoldLines(foldLines: Int) {
        if (mFoldLines == foldLines) {
            return
        }

        mFoldLines = min(maxLines, foldLines)
        formatText(mOriginText, mBufferType)
    }
}