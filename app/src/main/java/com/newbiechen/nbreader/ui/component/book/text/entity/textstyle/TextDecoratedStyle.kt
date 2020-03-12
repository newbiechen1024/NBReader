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

    private var isNotCached = true

    private var mFontSize: Int = 0
    private var mSpaceBefore: Int = 0
    private var mSpaceAfter: Int = 0
    private var mVerticalAlign: Int = 0
    private var isVerticallyAligned: Boolean? = null
    private var mLeftMargin: Int = 0
    private var mRightMargin: Int = 0
    private var mLeftPadding: Int = 0
    private var mRightPadding: Int = 0
    private var mFirstLineIndent: Int = 0
    private var mMetrics: TextMetrics? = null

    private fun initCache() {
        // mFontEntries = getFontEntriesInternal()
        isItalic = isItalicInternal()
        isBold = isBoldInternal()
        isUnderline = isUnderlineInternal()
        isStrikeThrough = isStrikeThroughInternal()
        mLineSpacePercent = getLineSpacePercentInternal()

        isNotCached = false
    }

    private fun initMetricsCache(metrics: TextMetrics) {
        mMetrics = metrics
        mFontSize = getFontSizeInternal(metrics)
        mSpaceBefore = getSpaceBeforeInternal(metrics, mFontSize)
        mSpaceAfter = getSpaceAfterInternal(metrics, mFontSize)
        mVerticalAlign = getVerticalAlignInternal(metrics, mFontSize)
        mLeftMargin = getLeftMarginInternal(metrics, mFontSize)
        mRightMargin = getRightMarginInternal(metrics, mFontSize)
        mLeftPadding = getLeftPaddingInternal(metrics, mFontSize)
        mRightPadding = getRightPaddingInternal(metrics, mFontSize)
        mFirstLineIndent = getFirstLineIndentInternal(metrics, mFontSize)
    }


/*    fun getFontEntries(): List<FontEntry> {
        if (isNotCached) {
            initCache()
        }
        return mFontEntries
    }

    protected abstract fun getFontEntriesInternal(): List<FontEntry>*/

    override fun getFontSize(metrics: TextMetrics): Int {
        if (metrics != mMetrics) {
            initMetricsCache(metrics)
        }
        return mFontSize
    }

    protected abstract fun getFontSizeInternal(metrics: TextMetrics): Int

    override fun getSpaceBefore(metrics: TextMetrics): Int {
        if (metrics != mMetrics) {
            initMetricsCache(metrics)
        }
        return mSpaceBefore
    }

    protected abstract fun getSpaceBeforeInternal(metrics: TextMetrics, fontSize: Int): Int

    override fun getSpaceAfter(metrics: TextMetrics): Int {
        if (metrics != mMetrics) {
            initMetricsCache(metrics)
        }
        return mSpaceAfter
    }

    protected abstract fun getSpaceAfterInternal(metrics: TextMetrics, fontSize: Int): Int

    override fun isItalic(): Boolean {
        if (isNotCached) {
            initCache()
        }
        return isItalic
    }

    protected abstract fun isItalicInternal(): Boolean

    override fun isBold(): Boolean {
        if (isNotCached) {
            initCache()
        }
        return isBold
    }

    protected abstract fun isBoldInternal(): Boolean

    override fun isUnderline(): Boolean {
        if (isNotCached) {
            initCache()
        }
        return isUnderline
    }

    protected abstract fun isUnderlineInternal(): Boolean

    override fun isStrikeThrough(): Boolean {
        if (isNotCached) {
            initCache()
        }
        return isStrikeThrough
    }

    protected abstract fun isStrikeThroughInternal(): Boolean

    override fun getVerticalAlign(metrics: TextMetrics): Int {
        if (metrics != mMetrics) {
            initMetricsCache(metrics)
        }
        return mVerticalAlign
    }

    protected abstract fun getVerticalAlignInternal(metrics: TextMetrics, fontSize: Int): Int

    override fun isVerticallyAligned(): Boolean {
        if (isVerticallyAligned == null) {
            isVerticallyAligned = parent!!.isVerticallyAligned() || isVerticallyAlignedInternal()
        }
        return isVerticallyAligned!!
    }

    protected abstract fun isVerticallyAlignedInternal(): Boolean

    override fun getLeftMargin(metrics: TextMetrics): Int {
        if (metrics != mMetrics) {
            initMetricsCache(metrics)
        }
        return mLeftMargin
    }

    protected abstract fun getLeftMarginInternal(metrics: TextMetrics, fontSize: Int): Int

    override fun getRightMargin(metrics: TextMetrics): Int {
        if (metrics != mMetrics) {
            initMetricsCache(metrics)
        }
        return mRightMargin
    }

    protected abstract fun getRightMarginInternal(metrics: TextMetrics, fontSize: Int): Int

    override fun getLeftPadding(metrics: TextMetrics): Int {
        if (metrics != mMetrics) {
            initMetricsCache(metrics)
        }
        return mLeftPadding
    }

    protected abstract fun getLeftPaddingInternal(metrics: TextMetrics, fontSize: Int): Int

    override fun getRightPadding(metrics: TextMetrics): Int {
        if (metrics != mMetrics) {
            initMetricsCache(metrics)
        }
        return mRightPadding
    }

    protected abstract fun getRightPaddingInternal(metrics: TextMetrics, fontSize: Int): Int

    override fun getFirstLineIndent(metrics: TextMetrics): Int {
        if (metrics != mMetrics) {
            initMetricsCache(metrics)
        }
        return mFirstLineIndent
    }

    protected abstract fun getFirstLineIndentInternal(metrics: TextMetrics, fontSize: Int): Int

    override fun getLineSpacePercent(): Int {
        if (isNotCached) {
            initCache()
        }
        return mLineSpacePercent
    }

    protected abstract fun getLineSpacePercentInternal(): Int
}