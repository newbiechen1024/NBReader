package com.newbiechen.nbreader.ui.component.book.text.entity.textstyle

import com.newbiechen.nbreader.ui.component.book.text.entity.TextMetrics

/**
 *  author : newbiechen
 *  date : 2019-10-26 16:04
 *  description :对传入的 TextKind 的装饰
 */

abstract class TextDecoratedStyle(parent: TreeTextStyle) : TreeTextStyle(parent) {
    // private var mFontEntries: List<FontEntry>? = null
    private var isItalic: Boolean = false
    private var isBold: Boolean = false
    private var isUnderline: Boolean = false
    private var isStrikeThrough: Boolean = false
    private var mLineSpacePercent: Int = 0
    private var mAlignmentType: Byte = TextAlignmentType.ALIGN_LEFT
    private var allowHyphenations: Boolean = false

    private var isNotCached: Boolean = true

    private var mFontSize: Int = 0
    private var mSpaceBefore: Int = 0
    private var mSpaceAfter: Int = 0
    private var mVerticalAlign: Int = 0
    private var isVerticallyAligned: Boolean = false
    private var mLeftMargin: Int = 0
    private var mRightMargin: Int = 0
    private var mLeftPadding: Int = 0
    private var mRightPadding: Int = 0
    private var mFirstLineIndent: Int = 0
    private var mMetrics: TextMetrics? = null

    // 保证持有数据
    override val parent: TreeTextStyle get() = super.parent!!

    private fun initCache() {
        // mFontEntries = getFontEntriesInternal()
        isItalic = isItalicInternal() ?: parent.isItalic()
        isBold = isBoldInternal() ?: parent.isBold()
        isUnderline = isUnderlineInternal() ?: parent.isUnderline()
        isStrikeThrough = isStrikeThroughInternal() ?: parent.isStrikeThrough()
        mLineSpacePercent = getLineSpacePercentInternal() ?: parent.getLineSpacePercent()
        isVerticallyAligned = isVerticallyAlignedInternal() ?: parent.isVerticallyAligned()
        mAlignmentType = getAlignmentInternal() ?: parent.getAlignment()
        allowHyphenations = allowHyphenationsInternal() ?: parent.allowHyphenations()

        isNotCached = false
    }

    private fun initMetricsCache(metrics: TextMetrics) {
        mMetrics = metrics
        mFontSize = getFontSizeInternal(metrics) ?: parent.getFontSize(metrics)
        mSpaceBefore = getSpaceBeforeInternal(metrics, mFontSize) ?: parent.getSpaceBefore(metrics)
        mSpaceAfter = getSpaceAfterInternal(metrics, mFontSize) ?: parent.getSpaceAfter(metrics)
        mVerticalAlign =
            getVerticalAlignInternal(metrics, mFontSize) ?: parent.getVerticalAlign(metrics)
        mLeftMargin = getLeftMarginInternal(metrics, mFontSize) ?: parent.getLeftMargin(metrics)
        mRightMargin = getRightMarginInternal(metrics, mFontSize) ?: parent.getRightMargin(metrics)
        mLeftPadding = getLeftPaddingInternal(metrics, mFontSize) ?: parent.getLeftPadding(metrics)
        mRightPadding =
            getRightPaddingInternal(metrics, mFontSize) ?: parent.getRightPadding(metrics)
        mFirstLineIndent =
            getFirstLineIndentInternal(metrics, mFontSize) ?: parent.getFirstLineIndent(metrics)
    }

    protected abstract fun getFontSizeInternal(metrics: TextMetrics): Int?

    protected abstract fun getSpaceBeforeInternal(metrics: TextMetrics, fontSize: Int): Int?

    protected abstract fun getSpaceAfterInternal(metrics: TextMetrics, fontSize: Int): Int?

    protected abstract fun isItalicInternal(): Boolean?

    protected abstract fun isBoldInternal(): Boolean?

    protected abstract fun isUnderlineInternal(): Boolean?

    protected abstract fun isStrikeThroughInternal(): Boolean?

    protected abstract fun isVerticallyAlignedInternal(): Boolean?

    protected abstract fun getAlignmentInternal(): Byte?

    protected abstract fun allowHyphenationsInternal(): Boolean?

    protected abstract fun getVerticalAlignInternal(metrics: TextMetrics, fontSize: Int): Int?

    protected abstract fun getLeftMarginInternal(metrics: TextMetrics, fontSize: Int): Int?

    protected abstract fun getRightMarginInternal(metrics: TextMetrics, fontSize: Int): Int?

    protected abstract fun getLeftPaddingInternal(metrics: TextMetrics, fontSize: Int): Int?

    protected abstract fun getRightPaddingInternal(metrics: TextMetrics, fontSize: Int): Int?

    protected abstract fun getFirstLineIndentInternal(metrics: TextMetrics, fontSize: Int): Int?

    protected abstract fun getLineSpacePercentInternal(): Int?

/*    fun getFontEntries(): List<FontEntry> {
        if (isNotCached) {
            initCache()
        }
        return mFontEntries
    }

    protected abstract fun getFontEntriesInternal(): List<FontEntry>*/

    final override fun getFontSize(metrics: TextMetrics): Int {
        if (metrics != mMetrics) {
            initMetricsCache(metrics)
        }
        return mFontSize
    }


    final override fun getSpaceBefore(metrics: TextMetrics): Int {
        if (metrics != mMetrics) {
            initMetricsCache(metrics)
        }
        return mSpaceBefore
    }


    final override fun getSpaceAfter(metrics: TextMetrics): Int {
        if (metrics != mMetrics) {
            initMetricsCache(metrics)
        }
        return mSpaceAfter
    }


    final override fun isItalic(): Boolean {
        if (isNotCached) {
            initCache()
        }
        return isItalic
    }


    final override fun isBold(): Boolean {
        if (isNotCached) {
            initCache()
        }
        return isBold
    }


    final override fun isUnderline(): Boolean {
        if (isNotCached) {
            initCache()
        }
        return isUnderline
    }


    final override fun isStrikeThrough(): Boolean {
        if (isNotCached) {
            initCache()
        }
        return isStrikeThrough
    }


    final override fun getVerticalAlign(metrics: TextMetrics): Int {
        if (metrics != mMetrics) {
            initMetricsCache(metrics)
        }
        return mVerticalAlign
    }


    final override fun isVerticallyAligned(): Boolean {
        if (isNotCached) {
            initCache()
        }
        return isVerticallyAligned
    }


    final override fun getAlignment(): Byte {
        if (isNotCached) {
            initCache()
        }
        return mAlignmentType
    }

    final override fun allowHyphenations(): Boolean {
        if (isNotCached) {
            initCache()
        }
        return allowHyphenations
    }


    final override fun getLeftMargin(metrics: TextMetrics): Int {
        if (metrics != mMetrics) {
            initMetricsCache(metrics)
        }
        return mLeftMargin
    }


    final override fun getRightMargin(metrics: TextMetrics): Int {
        if (metrics != mMetrics) {
            initMetricsCache(metrics)
        }
        return mRightMargin
    }


    final override fun getLeftPadding(metrics: TextMetrics): Int {
        if (metrics != mMetrics) {
            initMetricsCache(metrics)
        }
        return mLeftPadding
    }


    final override fun getRightPadding(metrics: TextMetrics): Int {
        if (metrics != mMetrics) {
            initMetricsCache(metrics)
        }
        return mRightPadding
    }


    final override fun getFirstLineIndent(metrics: TextMetrics): Int {
        if (metrics != mMetrics) {
            initMetricsCache(metrics)
        }
        return mFirstLineIndent
    }


    final override fun getLineSpacePercent(): Int {
        if (isNotCached) {
            initCache()
        }
        return mLineSpacePercent
    }
}