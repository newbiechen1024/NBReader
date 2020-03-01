package com.newbiechen.nbreader.ui.component.book.text.entity.textstyle

import com.newbiechen.nbreader.ui.component.book.text.entity.TextMetrics

/**
 *  author : newbiechen
 *  date : 2019-10-26 16:27
 *  description :自定义的文本装饰样式。
 *  解释：
 *
 *  1. 文本中的样式标记是在解析过程中，可自行添加的 Control 标记位
 *  2. 获取该标记位后去匹配自定义的 style.css 文件
 */

class CustomTextDecoratedStyle(
    parent: TextStyle,
    private val description: TextDecoratedStyleDescription
) :
    TextDecoratedStyle(parent) {

    override fun getFontSizeInternal(metrics: TextMetrics): Int {
        return description.getFontSize(metrics, parent.getFontSize(metrics))
    }

    override fun getSpaceBeforeInternal(metrics: TextMetrics, fontSize: Int): Int {
        return description.getSpaceBefore(metrics, parent.getSpaceBefore(metrics), fontSize)
    }

    override fun getSpaceAfterInternal(metrics: TextMetrics, fontSize: Int): Int {
        return description.getSpaceAfter(metrics, parent.getSpaceAfter(metrics), fontSize)
    }

    override fun isItalicInternal(): Boolean {
        return description.isItalic() ?: parent.isItalic()
    }

    override fun isBoldInternal(): Boolean {
        return description.isBold() ?: parent.isBold()
    }

    override fun isUnderlineInternal(): Boolean {
        return description.isUnderlined() ?: parent.isUnderline()
    }

    override fun isStrikeThroughInternal(): Boolean {
        return description.isStrikedThrough() ?: parent.isStrikeThrough()
    }

    override fun getVerticalAlignInternal(metrics: TextMetrics, fontSize: Int): Int {
        return description.getVerticalAlign(metrics, parent.getVerticalAlign(metrics), fontSize)
    }

    override fun isVerticallyAlignedInternal(): Boolean {
        return description.hasNonZeroVerticalAlign()
    }

    override fun getLeftMarginInternal(metrics: TextMetrics, fontSize: Int): Int {
        return description.getLeftMargin(metrics, parent.getLeftMargin(metrics), fontSize)
    }

    override fun getRightMarginInternal(metrics: TextMetrics, fontSize: Int): Int {
        return description.getRightMargin(metrics, parent.getRightMargin(metrics), fontSize)
    }

    override fun getLeftPaddingInternal(metrics: TextMetrics, fontSize: Int): Int {
        return description.getLeftPadding(metrics, parent.getLeftPadding(metrics), fontSize)
    }

    override fun getRightPaddingInternal(metrics: TextMetrics, fontSize: Int): Int {
        return description.getRightPadding(metrics, parent.getRightPadding(metrics), fontSize)
    }

    override fun getFirstLineIndentInternal(metrics: TextMetrics, fontSize: Int): Int {
        return description.getFirstLineIndent(metrics, parent.getFirstLineIndent(metrics), fontSize)
    }

    override fun getLineSpacePercentInternal(): Int {
        val lineHeight = description.getLineHeight()
        return if (lineHeight == null || !lineHeight.matches("[1-9][0-9]*%".toRegex())) {
            parent.getLineSpacePercent()
        } else Integer.valueOf(lineHeight.substring(0, lineHeight.length - 1))
    }

    override fun getAlignment(): Byte {
        val defined = description.getAlignment()
        return if (defined != TextAlignmentType.ALIGN_UNDEFINED) {
            defined
        } else parent.getAlignment()
    }

    override fun allowHyphenations(): Boolean {
        return description.allowHyphenations() ?: parent.allowHyphenations()
    }
}