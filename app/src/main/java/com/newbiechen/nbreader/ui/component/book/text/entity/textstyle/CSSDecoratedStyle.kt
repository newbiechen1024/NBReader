package com.newbiechen.nbreader.ui.component.book.text.entity.textstyle

import com.newbiechen.nbreader.ui.component.book.text.config.CSSStyleInterceptor
import com.newbiechen.nbreader.ui.component.book.text.entity.TextMetrics
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.*

/**
 *  author : newbiechen
 *  date : 2020/3/11 3:11 PM
 *  description :CSS style 的装饰 style
 */

class CSSDecoratedStyle(
    parent: TreeTextStyle,
    private val styleTag: TextStyleTag,
    private val interceptor: CSSStyleInterceptor?
) : TreeTextStyle(parent) {

    private var mTreeParent: TreeTextStyle? = null

    private fun getTreeParent(): TreeTextStyle {
        if (mTreeParent == null) {
            mTreeParent = computeTreeParent()
        }
        return mTreeParent!!
    }

    private fun computeTreeParent(): TreeTextStyle {
        // 如果当前样式的深度为 0
        if (styleTag.getDepth() == 0.toByte()) {
            return parent!!.parent ?: parent
        }

        var count = 0
        var p = parent!!

        while (p != p.parent) {
            if (p is CSSDecoratedStyle) {
                if (p.styleTag.getDepth() != styleTag.getDepth()) {
                    return p
                }
            } else {
                if (++count > 1) {
                    return p
                }
            }
            p = p.parent!!
        }
        return p
    }

    override fun getFontSize(metrics: TextMetrics): Int {
        val isEnable = interceptor?.isEnableFontSize() ?: false

        if (styleTag is TextCssStyleTag && !isEnable) {
            return parent!!.getFontSize(metrics)
        }

        return if (styleTag.isFeatureSupported(TextFeature.LENGTH_FONT_SIZE)) {
            val baseFontSize = getTreeParent().getFontSize(metrics)

            // TODO:值需要修改
            if (styleTag.isFeatureSupported(TextFeature.FONT_STYLE_MODIFIER)) {
                if (styleTag.getFontModifier(TextFontModifier.FONT_MODIFIER_INHERIT) == true) {
                    return baseFontSize
                }
                if (styleTag.getFontModifier(TextFontModifier.FONT_MODIFIER_LARGER) == true) {
                    return baseFontSize * 120 / 100
                }
                if (styleTag.getFontModifier(TextFontModifier.FONT_MODIFIER_SMALLER) == true) {
                    return baseFontSize * 100 / 120
                }
            }

            styleTag.getLength(TextFeature.LENGTH_FONT_SIZE, metrics, baseFontSize)
        } else {
            parent!!.getFontSize(metrics)
        }
    }

    override fun isBold(): Boolean {
        val result = styleTag.getFontModifier(TextFontModifier.FONT_MODIFIER_BOLD)
        return result ?: parent!!.isBold()
    }

    override fun isItalic(): Boolean {
        val result = styleTag.getFontModifier(TextFontModifier.FONT_MODIFIER_ITALIC)
        return result ?: parent!!.isItalic()
    }

    override fun isUnderline(): Boolean {
        val result = styleTag.getFontModifier(TextFontModifier.FONT_MODIFIER_UNDERLINED)
        return result ?: parent!!.isUnderline()
    }

    override fun isStrikeThrough(): Boolean {
        val result = styleTag.getFontModifier(TextFontModifier.FONT_MODIFIER_STRIKEDTHROUGH)
        return result ?: parent!!.isStrikeThrough()
    }

    override fun getLeftMargin(metrics: TextMetrics): Int {
        val isEnable = interceptor?.isEnableMargin() ?: false

        if (styleTag is TextCssStyleTag && !isEnable) {
            return parent!!.getLeftMargin(metrics)
        }
        return if (styleTag.isFeatureSupported(TextFeature.LENGTH_MARGIN_LEFT)) {
            getTreeParent().getLeftMargin(metrics) + styleTag.getLength(
                TextFeature.LENGTH_MARGIN_LEFT,
                metrics,
                getFontSize(metrics)
            )
        } else {
            parent!!.getLeftMargin(metrics)
        }
    }

    override fun getRightMargin(metrics: TextMetrics): Int {
        val isEnable = interceptor?.isEnableMargin() ?: false
        if (styleTag is TextCssStyleTag && !isEnable) {
            return parent!!.getRightMargin(metrics)
        }

        return if (styleTag.isFeatureSupported(TextFeature.LENGTH_MARGIN_RIGHT)) {
            getTreeParent().getRightMargin(metrics) + styleTag.getLength(
                TextFeature.LENGTH_MARGIN_RIGHT,
                metrics,
                getFontSize(metrics)
            )
        } else {
            parent!!.getRightMargin(metrics)
        }
    }

    override fun getLeftPadding(metrics: TextMetrics): Int {
        val isEnable = interceptor?.isEnablePadding() ?: false

        if (styleTag is TextCssStyleTag && !isEnable) {
            return parent!!.getLeftPadding(metrics)
        }

        return if (styleTag.isFeatureSupported(TextFeature.LENGTH_PADDING_LEFT)) {
            getTreeParent().getLeftPadding(metrics) + styleTag.getLength(
                TextFeature.LENGTH_PADDING_LEFT,
                metrics,
                getFontSize(metrics)
            )

        } else {
            parent!!.getLeftPadding(metrics)
        }
    }

    override fun getRightPadding(metrics: TextMetrics): Int {
        val isEnable = interceptor?.isEnablePadding() ?: false
        if (styleTag is TextCssStyleTag && !isEnable) {
            return parent!!.getRightPadding(metrics)
        }
        return if (styleTag.isFeatureSupported(TextFeature.LENGTH_PADDING_RIGHT)) {
            getTreeParent().getRightPadding(metrics) + styleTag.getLength(
                TextFeature.LENGTH_PADDING_RIGHT,
                metrics,
                getFontSize(metrics)
            )
        } else {
            parent!!.getRightPadding(metrics)
        }
    }

    override fun getFirstLineIndent(metrics: TextMetrics): Int {
        return if (!styleTag.isFeatureSupported(TextFeature.LENGTH_FIRST_LINE_INDENT)) {
            parent!!.getFirstLineIndent(metrics)
        } else {
            styleTag.getLength(TextFeature.LENGTH_FIRST_LINE_INDENT, metrics, metrics.fontSize)
        }

    }

    override fun getLineSpacePercent(): Int {
        return parent!!.getLineSpacePercent()
    }

    override fun getVerticalAlign(metrics: TextMetrics): Int {
        return when {
            styleTag.isFeatureSupported(TextFeature.LENGTH_VERTICAL_ALIGN) -> styleTag.getLength(
                TextFeature.LENGTH_VERTICAL_ALIGN,
                metrics,
                getFontSize(metrics)
            )
            styleTag.isFeatureSupported(TextFeature.NON_LENGTH_VERTICAL_ALIGN) -> when (styleTag.getVerticalAlignCode()) {
                // sub
                0.toByte() -> TextStyleTag.compute(
                    TextStyleTag.Length((-50).toShort(), TextSizeUnit.EM_100),
                    metrics, getFontSize(metrics), TextFeature.LENGTH_VERTICAL_ALIGN
                )
                // super
                1.toByte() -> TextStyleTag.compute(
                    TextStyleTag.Length(50.toShort(), TextSizeUnit.EM_100),
                    metrics, getFontSize(metrics), TextFeature.LENGTH_VERTICAL_ALIGN
                )

                else -> parent!!.getVerticalAlign(metrics)
            }
            else -> parent!!.getVerticalAlign(metrics)
        }
    }

    override fun isVerticallyAligned(): Boolean {
        return when {
            styleTag.isFeatureSupported(TextFeature.LENGTH_VERTICAL_ALIGN) -> styleTag.hasNonZeroLength(
                TextFeature.LENGTH_VERTICAL_ALIGN
            )
            styleTag.isFeatureSupported(TextFeature.NON_LENGTH_VERTICAL_ALIGN) -> when (styleTag.getVerticalAlignCode()) {
                0.toByte(), 1.toByte() -> true
                else -> false
            }
            else -> false
        }
    }

    override fun getSpaceBefore(metrics: TextMetrics): Int {
        val isEnable = interceptor?.isEnableSpace() ?: false
        if (styleTag is TextCssStyleTag && !isEnable) {
            return parent!!.getSpaceBefore(metrics)
        }
        return if (!styleTag.isFeatureSupported(TextFeature.LENGTH_SPACE_BEFORE)) {
            parent!!.getSpaceBefore(metrics)
        } else styleTag.getLength(TextFeature.LENGTH_SPACE_BEFORE, metrics, getFontSize(metrics))
    }

    override fun getSpaceAfter(metrics: TextMetrics): Int {
        val isEnable = interceptor?.isEnableSpace() ?: false
        if (styleTag is TextCssStyleTag && !isEnable) {
            return parent!!.getSpaceAfter(metrics)
        }

        return if (!styleTag.isFeatureSupported(TextFeature.LENGTH_SPACE_AFTER)) {
            parent!!.getSpaceAfter(metrics)
        } else styleTag.getLength(TextFeature.LENGTH_SPACE_AFTER, metrics, getFontSize(metrics))
    }

    override fun getAlignment(): Byte {
        val isEnable = interceptor?.isEnableAlignment() ?: false

        if (styleTag is TextCssStyleTag && !isEnable) {
            return parent!!.getAlignment()
        }

        return if (styleTag.isFeatureSupported(TextFeature.ALIGNMENT_TYPE)) {
            styleTag.getAlignmentType()
        } else {
            parent!!.getAlignment()
        }
    }

    override fun allowHyphenations(): Boolean {
        // TODO:
        return parent!!.allowHyphenations()
    }
}