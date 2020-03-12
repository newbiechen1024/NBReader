package com.newbiechen.nbreader.ui.component.book.text.entity.textstyle

import com.newbiechen.nbreader.ui.component.book.text.entity.TextMetrics

/**
 *  author : newbiechen
 *  date : 2020/3/11 3:10 PM
 *  description :control style 的装饰 style
 */

class ControlDecoratedStyle(
    // 父对象
    parent: TreeTextStyle,
    // 控制对象
    controlStyle: ControlTextStyle?,
    // 拦截器对象
    interceptorStyle: ControlInterceptorStyle?
) : TextDecoratedStyle(parent) {

    private var mTextStyleChain: NullableTextStyle

    init {
        // 创建责任链
        var chain: NullableTextStyle = parent

        if (controlStyle != null) {
            chain = TextStyleChain(controlStyle, chain)
        }

        if (interceptorStyle != null) {
            chain = TextStyleChain(interceptorStyle, chain)
        }
        mTextStyleChain = chain
    }

    override fun getFontSizeInternal(metrics: TextMetrics): Int {
        return mTextStyleChain.getFontSize(metrics)!!
    }

    override fun isBoldInternal(): Boolean {
        return mTextStyleChain.isBold()!!
    }

    override fun isItalicInternal(): Boolean {
        return mTextStyleChain.isItalic()!!
    }

    override fun isUnderlineInternal(): Boolean {
        return mTextStyleChain.isUnderline()!!
    }

    override fun isStrikeThroughInternal(): Boolean {
        return mTextStyleChain.isStrikeThrough()!!
    }

    override fun getLeftMarginInternal(metrics: TextMetrics, fontSize: Int): Int {
        return mTextStyleChain.getLeftMargin(metrics)!!
    }

    override fun getRightMarginInternal(metrics: TextMetrics, fontSize: Int): Int {
        return mTextStyleChain.getRightMargin(metrics)!!
    }

    override fun getLeftPaddingInternal(metrics: TextMetrics, fontSize: Int): Int {
        return mTextStyleChain.getLeftPadding(metrics)!!
    }

    override fun getRightPaddingInternal(metrics: TextMetrics, fontSize: Int): Int {
        return mTextStyleChain.getRightPadding(metrics)!!
    }

    override fun getFirstLineIndentInternal(metrics: TextMetrics, fontSize: Int): Int {
        return mTextStyleChain.getFirstLineIndent(metrics)!!
    }

    override fun getLineSpacePercentInternal(): Int {
        return mTextStyleChain.getLineSpacePercent()!!
    }

    override fun getVerticalAlignInternal(metrics: TextMetrics, fontSize: Int): Int {
        return mTextStyleChain.getVerticalAlign(metrics)!!
    }

    override fun isVerticallyAlignedInternal(): Boolean {
        return mTextStyleChain.isVerticallyAligned()!!
    }

    override fun getSpaceBeforeInternal(metrics: TextMetrics, fontSize: Int): Int {
        return mTextStyleChain.getSpaceBefore(metrics)!!
    }

    override fun getSpaceAfterInternal(metrics: TextMetrics, fontSize: Int): Int {
        return mTextStyleChain.getSpaceAfter(metrics)!!
    }

    override fun getAlignment(): Byte {
        return mTextStyleChain.getAlignment()!!
    }

    override fun allowHyphenations(): Boolean {
        return mTextStyleChain.allowHyphenations()!!
    }
}