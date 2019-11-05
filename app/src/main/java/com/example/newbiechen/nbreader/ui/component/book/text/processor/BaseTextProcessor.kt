package com.example.newbiechen.nbreader.ui.component.book.text.processor

import android.graphics.Canvas
import android.util.Size
import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextMetrics
import com.example.newbiechen.nbreader.ui.component.book.text.entity.element.TextControlElement
import com.example.newbiechen.nbreader.ui.component.book.text.entity.element.TextElement
import com.example.newbiechen.nbreader.ui.component.book.text.entity.element.TextStyleElement
import com.example.newbiechen.nbreader.ui.component.book.text.entity.element.TextWordElement
import com.example.newbiechen.nbreader.ui.component.book.text.entity.textstyle.CustomTextDecoratedStyle
import com.example.newbiechen.nbreader.ui.component.book.text.entity.textstyle.ExplicitTextDecoratedStyle
import com.example.newbiechen.nbreader.ui.component.book.text.entity.textstyle.TextStyle
import com.example.newbiechen.nbreader.ui.component.widget.page.PageType

/**
 *  author : newbiechen
 *  date : 2019-10-24 18:16
 *  description :文本处理器基础方法封装
 */
abstract class BaseTextProcessor {
    /**
     * 视口宽高
     */
    var viewWidth: Int = 0
        private set
    var viewHeight: Int = 0
        private set

    // 文本配置项
    protected var textConfig: TextConfig = DefaultTextConfig()
        private set

    // 文本画笔
    private var mPaintContext: TextPaintContext = TextPaintContext()

    // 使用的文本样式
    private var mTextStyle: TextStyle? = null

    private var mWordHeight: Int? = null

    protected abstract fun drawInternal(canvas: TextCanvas, pageType: PageType)

    // 绘制传入的页面
    fun draw(canvas: Canvas, pageType: PageType) {

        drawInternal(TextCanvas(canvas), pageType)
    }

    // TODO:视口因为 onSizeChanged 改变怎么办？
    fun setViewPort(width: Int, height: Int) {
        viewWidth = width
        viewHeight = height
    }

    fun setConfig(config: TextConfig) {
        // 重新进行样式初始化操作
    }

    /**
     * 获取文本绘制区域
     */
    fun getTextAreaSize(): Size {
        return Size(getTextAreaWidth(), getTextAreaHeight())
    }

    /**
     * 获取文本区域宽
     */
    fun getTextAreaWidth(): Int {
        return viewWidth - textConfig.getLeftMargin() - textConfig.getRightMargin()
    }

    /**
     * 获取文本区域高
     */
    fun getTextAreaHeight(): Int {
        return viewHeight - textConfig.getTopMargin() - textConfig.getBottomMargin()
    }

    /**
     * 获取当前文本样式
     */
    protected fun getTextStyle(): TextStyle {
        if (mTextStyle == null) {
            resetTextStyle()
        }
        return mTextStyle!!
    }

    /**
     * 设置当前文本样式
     */
    protected fun setTextStyle(style: TextStyle) {
        if (mTextStyle != style) {
            mTextStyle = style
            mWordHeight = -1
        }

        mPaintContext.setFont(style.getFontSize())
    }

    /**
     * 重置文本样式
     */
    protected fun resetTextStyle() {
        setTextStyle(textConfig.getTextStyle())
    }

    /**
     * 是否样式类型元素如
     * @see
     */
    fun isStyleElement(element: TextElement): Boolean {
        return element === TextElement.StyleClose ||
                element is TextStyleElement ||
                element is TextControlElement
    }

    /**
     * 应用样式类型元素
     */
    fun applyStyleElement(element: TextElement) {
        when {
            element === TextElement.StyleClose -> applyStyleClose()
            element is TextStyleElement -> applyStyle(element)
            element is TextControlElement -> applyControl(element)
        }
    }

    private fun applyStyleClose() {
        setTextStyle(mTextStyle!!.parent)
    }

    private fun applyStyle(element: TextStyleElement) {
        setTextStyle(ExplicitTextDecoratedStyle(mTextStyle!!, element.styleEntry))
    }

    private fun applyControl(control: TextControlElement) {
        if (control.isStart) {
            // 暂时不处理超链接
/*            val hyperlink = if (control is TextHyperlinkControlElement)
                (control as TextHyperlinkControlElement).Hyperlink
            else null*/

            val description = textConfig.getTextDecoratedStyleDesc(control.styleType)
            if (description != null) {
                setTextStyle(CustomTextDecoratedStyle(mTextStyle!!, description))
            }
        } else {
            setTextStyle(mTextStyle!!.parent)
        }
    }

    /**
     * 获取画笔
     */
    protected fun getPaintContext(): TextPaintContext {
        return mPaintContext
    }

    private var mMetrics: TextMetrics? = null

    // 文本大小的指示
    protected fun getMetrics(): TextMetrics {
        // this local variable is used to guarantee null will not
        // be returned from this method enen in multi-thread environment
        var m: TextMetrics? = mMetrics
        if (m == null) {
            m = TextMetrics(
                ibrary.Instance().getDisplayDPI(), // dpi
                // TODO: screen area width
                100, // 屏幕宽
                // TODO: screen area height
                100, // 屏幕高
                textConfig.getTextStyle().getFontSize() // 获取文字的大小
            )
            mMetrics = m
        }
        return m
    }

    /**
     * 计算元素的宽
     */
    // 获取文本元素可用的宽度
    protected fun getElementWidth(element: TextElement, charIndex: Int): Int {
        return when {
            element is TextWordElement -> getWordWidth(element, charIndex)
            element === TextElement.NBSpace -> getPaintContext().getSpaceWidth()
            element === TextElement.Indent -> mTextStyle!!.getFirstLineIndent(getMetrics())
            else -> 0
        }
    }

    /**
     * 计算元素的高
     */
    // 获取文本元素可用的高度
    protected fun getElementHeight(element: TextElement): Int {
        if (element === TextElement.NBSpace ||
            element is TextWordElement ||
            element is TextFixedHSpaceElement
        ) {
            return getWordHeight()
        }
        return 0
    }

    protected fun getElementDescent(element: TextElement): Int {
        return if (element is TextWordElement) getPaintContext().getDescent() else 0
    }

    /**
     * 获取单词的宽
     */
    protected fun getWordWidth(word: TextWordElement, start: Int): Int {
        return if (start == 0) {
            word.getWidth(getPaintContext())
        } else {
            getPaintContext().getStringWidth(word.data, word.offset + start, word.length - start)
        }
    }

    private var mWordPartArray = CharArray(20)

    fun getWordWidth(word: TextWordElement, start: Int, length: Int, addHyphenationSign: Boolean): Int {
        var length = length
        if (length == -1) {
            if (start == 0) {
                return word.getWidth(getPaintContext())
            }
            length = word.length - start
        }

        if (!addHyphenationSign) {
            return getPaintContext().getStringWidth(word.data, word.offset + start, length)
        }

        var part = mWordPartArray
        if (length + 1 > part.size) {
            part = CharArray(length + 1)
            mWordPartArray = part
        }

        System.arraycopy(word.data, word.offset + start, part, 0, length)
        part[length] = '-'
        return getPaintContext().getStringWidth(part, 0, length + 1)
    }


    /**
     * 获取单词的高度
     */
    protected fun getWordHeight(): Int {
        if (mWordHeight == null) {
            val textStyle = mTextStyle!!
            mWordHeight = getPaintContext().getStringHeight() * textStyle.getLineSpacePercent() / 100
            +textStyle.getVerticalAlign(
                getMetrics()
            )
        }
        return mWordHeight!!
    }

    protected fun drawWord(
        canvas: TextCanvas,
        x: Int,
        y: Int,
        word: TextWordElement,
        start: Int,
        length: Int,
        addHyphenationSign: Boolean,
        color: Int
    ) {
        if (start == 0 && length == -1) {
            drawString(canvas, x, y, word.data, word.offset, word.length, color, 0)
        } else {
            var length = length
            if (length == -1) {
                length = word.length - start
            }
            if (!addHyphenationSign) {
                drawString(canvas, x, y, word.data, word.offset + start, length, color, start)
            } else {
                var part = mWordPartArray
                if (length + 1 > part.size) {
                    part = CharArray(length + 1)
                    mWordPartArray = part
                }

                System.arraycopy(word.data, word.offset + start, part, 0, length)
                part[length] = '-'
                drawString(canvas, x, y, part, 0, length + 1, color, start)
            }
        }
    }

    protected fun drawString(
        canvas: TextCanvas,
        x: Int,
        y: Int,
        str: CharArray,
        offset: Int,
        length: Int,
        color: Int,
        shift: Int
    ) {
        // 设置文字显示的颜色
        getPaintContext().setTextColor(color)
        // 进行绘制
        canvas.drawString(x, y, str, offset, length, getPaintContext())
        // TODO:没有处理存在 Mark 的情况
    }
}