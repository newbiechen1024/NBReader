package com.newbiechen.nbreader.ui.component.book.text.entity.textstyle

import com.newbiechen.nbreader.ui.component.book.text.entity.TextMetrics
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextFeature.ALIGNMENT_TYPE
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextFeature.FONT_STYLE_MODIFIER
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextFeature.LENGTH_FIRST_LINE_INDENT
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextFeature.LENGTH_FONT_SIZE
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextFeature.LENGTH_MARGIN_LEFT
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextFeature.LENGTH_MARGIN_RIGHT
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextFeature.LENGTH_PADDING_LEFT
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextFeature.LENGTH_PADDING_RIGHT
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextFeature.LENGTH_SPACE_AFTER
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextFeature.LENGTH_SPACE_BEFORE
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextFeature.LENGTH_VERTICAL_ALIGN
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextFeature.NON_LENGTH_VERTICAL_ALIGN
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextFontModifier.FONT_MODIFIER_BOLD
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextFontModifier.FONT_MODIFIER_INHERIT
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextFontModifier.FONT_MODIFIER_ITALIC
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextFontModifier.FONT_MODIFIER_LARGER
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextFontModifier.FONT_MODIFIER_SMALLER
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextFontModifier.FONT_MODIFIER_STRIKEDTHROUGH
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextFontModifier.FONT_MODIFIER_UNDERLINED
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextSizeUnit
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.TextStyleTag

/**
 *  author : newbiechen
 *  date : 2019-10-26 16:28
 *  description :明确的文本装饰描述样式
 *
 *  解释：该样式标记是从标准的书籍(比如，Epub 中包含的 style.css 文件)中获取到的。
 */

class ExplicitTextDecoratedStyle(parent: TextStyle, private val styleTag: TextStyleTag) :
    TextDecoratedStyle(parent) {

    private var mTreeParent: TextStyle? = null

    private fun getTreeParent(): TextStyle {
        if (mTreeParent == null) {
            mTreeParent = computeTreeParent()
        }
        return mTreeParent!!
    }

    private fun computeTreeParent(): TextStyle {
        // 如果当前样式的深度为 0
        if (styleTag.depth == 0.toByte()) {
            return parent.parent
        }

        var count = 0
        var p = parent

        while (p != p.parent) {
            if (p is ExplicitTextDecoratedStyle) {
                if (p.styleTag.depth != styleTag.depth) {
                    return p
                }
            } else {
                if (++count > 1) {
                    return p
                }
            }
            p = p.parent
        }
        return p
    }


    override fun getFontSizeInternal(metrics: TextMetrics): Int {
        val baseFontSize = getTreeParent().getFontSize(metrics)
        if (styleTag.isFeatureSupported(FONT_STYLE_MODIFIER)) {
            if (styleTag.getFontModifier(FONT_MODIFIER_INHERIT) == true) {
                return baseFontSize
            }
            if (styleTag.getFontModifier(FONT_MODIFIER_LARGER) == true) {
                return baseFontSize * 120 / 100
            }
            if (styleTag.getFontModifier(FONT_MODIFIER_SMALLER) == true) {
                return baseFontSize * 100 / 120
            }
        }
        return if (styleTag.isFeatureSupported(LENGTH_FONT_SIZE)) {
            styleTag.getLength(LENGTH_FONT_SIZE, metrics, baseFontSize)
        } else {
            parent.getFontSize(metrics)
        }
    }

    override fun getSpaceBeforeInternal(metrics: TextMetrics, fontSize: Int): Int {
        return if (!styleTag.isFeatureSupported(LENGTH_SPACE_BEFORE)) {
            parent.getSpaceBefore(metrics)
        } else styleTag.getLength(LENGTH_SPACE_BEFORE, metrics, fontSize)
    }

    override fun getSpaceAfterInternal(metrics: TextMetrics, fontSize: Int): Int {
        return if (!styleTag.isFeatureSupported(LENGTH_SPACE_AFTER)) {
            parent.getSpaceAfter(metrics)
        } else styleTag.getLength(LENGTH_SPACE_AFTER, metrics, fontSize)
    }

    override fun isItalicInternal(): Boolean {
        val result = styleTag.getFontModifier(FONT_MODIFIER_ITALIC)
        return result ?: parent.isItalic()
    }

    override fun isBoldInternal(): Boolean {
        val result = styleTag.getFontModifier(FONT_MODIFIER_BOLD)
        return result ?: parent.isBold()
    }

    override fun isUnderlineInternal(): Boolean {
        val result = styleTag.getFontModifier(FONT_MODIFIER_UNDERLINED)
        return result ?: parent.isUnderline()
    }

    override fun isStrikeThroughInternal(): Boolean {
        val result = styleTag.getFontModifier(FONT_MODIFIER_STRIKEDTHROUGH)
        return result ?: parent.isStrikeThrough()
    }

    override fun getVerticalAlignInternal(metrics: TextMetrics, fontSize: Int): Int {
        return when {
            styleTag.isFeatureSupported(LENGTH_VERTICAL_ALIGN) -> styleTag.getLength(
                LENGTH_VERTICAL_ALIGN,
                metrics,
                fontSize
            )
            styleTag.isFeatureSupported(NON_LENGTH_VERTICAL_ALIGN) -> when (styleTag.getVerticalAlignCode()) {
                // sub
                0.toByte() -> TextStyleTag.compute(
                    TextStyleTag.Length((-50).toShort(), TextSizeUnit.EM_100),
                    metrics, fontSize, LENGTH_VERTICAL_ALIGN
                )
                // super
                1.toByte() -> TextStyleTag.compute(
                    TextStyleTag.Length(50.toShort(), TextSizeUnit.EM_100),
                    metrics, fontSize, LENGTH_VERTICAL_ALIGN
                )

                else -> parent.getVerticalAlign(metrics)
            }
            else -> parent.getVerticalAlign(metrics)
        }
    }

    override fun isVerticallyAlignedInternal(): Boolean {
        return when {
            styleTag.isFeatureSupported(LENGTH_VERTICAL_ALIGN) -> styleTag.hasNonZeroLength(
                LENGTH_VERTICAL_ALIGN
            )
            styleTag.isFeatureSupported(NON_LENGTH_VERTICAL_ALIGN) -> when (styleTag.getVerticalAlignCode()) {
                0.toByte(), 1.toByte() -> true
                else -> false
            }
            else -> false
        }
    }

    override fun getLeftMarginInternal(metrics: TextMetrics, fontSize: Int): Int {
        return if (!styleTag.isFeatureSupported(LENGTH_MARGIN_LEFT)) {
            parent.getLeftMargin(metrics)
        } else {
            getTreeParent().getLeftMargin(metrics) + styleTag.getLength(
                LENGTH_MARGIN_LEFT,
                metrics,
                fontSize
            )
        }
    }

    override fun getRightMarginInternal(metrics: TextMetrics, fontSize: Int): Int {
        return if (!styleTag.isFeatureSupported(LENGTH_MARGIN_RIGHT)) {
            parent.getRightMargin(metrics)
        } else {
            getTreeParent().getRightMargin(metrics) + styleTag.getLength(
                LENGTH_MARGIN_RIGHT,
                metrics,
                fontSize
            )
        }
    }

    override fun getLeftPaddingInternal(metrics: TextMetrics, fontSize: Int): Int {
        return if (!styleTag.isFeatureSupported(LENGTH_PADDING_LEFT)) {
            parent.getLeftPadding(metrics)
        } else getTreeParent().getLeftPadding(metrics) + styleTag.getLength(
            LENGTH_PADDING_LEFT,
            metrics,
            fontSize
        )
    }

    override fun getRightPaddingInternal(metrics: TextMetrics, fontSize: Int): Int {
        return if (!styleTag.isFeatureSupported(LENGTH_PADDING_RIGHT)) {
            parent.getRightPadding(metrics)
        } else getTreeParent().getRightPadding(metrics) + styleTag.getLength(
            LENGTH_PADDING_RIGHT,
            metrics,
            fontSize
        )
    }

    override fun getFirstLineIndentInternal(metrics: TextMetrics, fontSize: Int): Int {
        return if (!styleTag.isFeatureSupported(LENGTH_FIRST_LINE_INDENT)) {
            parent.getFirstLineIndent(metrics)
        } else styleTag.getLength(LENGTH_FIRST_LINE_INDENT, metrics, fontSize)
    }

    override fun getLineSpacePercentInternal(): Int {
        return parent.getLineSpacePercent()
    }

    override fun getAlignment(): Byte {
        return if (styleTag.isFeatureSupported(ALIGNMENT_TYPE)) {
            styleTag.getAlignmentType()
        } else {
            parent.getAlignment()
        }
    }

    override fun allowHyphenations(): Boolean {
        return parent.allowHyphenations()
    }
}