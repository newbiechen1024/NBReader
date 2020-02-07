package com.newbiechen.nbreader.ui.component.book.text.entity.entry

import com.newbiechen.nbreader.ui.component.book.text.entity.TextMetrics
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

/**
 *  author : newbiechen
 *  date : 2019-10-24 19:46
 *  description :文本样式组，参数基于 epub 的 style.css
 */

class TextStyleEntry(val depth: Short) { // 样式的深度

    /**
     * 当前 TextStyle 支持的标签样式
     */
    object Feature {
        const val LENGTH_PADDING_LEFT = 0
        const val LENGTH_PADDING_RIGHT = 1
        const val LENGTH_MARGIN_LEFT = 2
        const val LENGTH_MARGIN_RIGHT = 3
        const val LENGTH_FIRST_LINE_INDENT = 4
        const val LENGTH_SPACE_BEFORE = 5
        const val LENGTH_SPACE_AFTER = 6
        const val LENGTH_FONT_SIZE = 7
        const val LENGTH_VERTICAL_ALIGN = 8
        const val NUMBER_OF_LENGTHS = 9
        const val ALIGNMENT_TYPE = NUMBER_OF_LENGTHS
        const val FONT_FAMILY = NUMBER_OF_LENGTHS + 1
        const val FONT_STYLE_MODIFIER = NUMBER_OF_LENGTHS + 2
        const val NON_LENGTH_VERTICAL_ALIGN = NUMBER_OF_LENGTHS + 3
        // not transferred at the moment
        const val DISPLAY = NUMBER_OF_LENGTHS + 4
    }

    /**
     * 字体样式
     */
    object FontModifier {
        const val FONT_MODIFIER_BOLD = (1 shl 0).toByte()
        const val FONT_MODIFIER_ITALIC = (1 shl 1).toByte()
        const val FONT_MODIFIER_UNDERLINED = (1 shl 2).toByte()
        const val FONT_MODIFIER_STRIKEDTHROUGH = (1 shl 3).toByte()
        const val FONT_MODIFIER_SMALLCAPS = (1 shl 4).toByte()
        const val FONT_MODIFIER_INHERIT = (1 shl 5).toByte()
        const val FONT_MODIFIER_SMALLER = (1 shl 6).toByte()
        const val FONT_MODIFIER_LARGER = (1 shl 7).toByte()
    }

    /**
     * 尺寸单位
     */
    object SizeUnit {
        // TODO: add IN, CM, MM, PICA ("pc", = 12 POINT)
        const val PIXEL: Byte = 0
        const val POINT: Byte = 1
        const val EM_100: Byte = 2
        const val REM_100: Byte = 3
        const val EX_100: Byte = 4
        const val PERCENT: Byte = 5
    }

    /**
     * 带单位的长度
     */
    data class Length(val size: Short, val unit: Byte) {
        override fun toString(): String {
            return "$size.$unit"
        }
    }

    companion object {

        fun isFeatureSupported(mask: Short, featureId: Int): Boolean {
            return mask.toInt() and (1 shl featureId) != 0
        }

        private fun getFullSize(metrics: TextMetrics, fontsize: Int, featureId: Int): Int {
            return when (featureId) {
                Feature.LENGTH_MARGIN_LEFT, Feature.LENGTH_MARGIN_RIGHT,
                Feature.LENGTH_PADDING_LEFT, Feature.LENGTH_PADDING_RIGHT,
                Feature.LENGTH_FIRST_LINE_INDENT -> metrics.fullWidth

                Feature.LENGTH_SPACE_BEFORE, Feature.LENGTH_SPACE_AFTER -> metrics.fullHeight

                Feature.LENGTH_VERTICAL_ALIGN, Feature.LENGTH_FONT_SIZE -> fontsize

                else -> metrics.fullWidth
            }
        }

        fun compute(length: Length, metrics: TextMetrics, fontsize: Int, featureId: Int): Int {
            return when (length.unit) {
                SizeUnit.PIXEL -> length.size.toInt()
                SizeUnit.POINT -> length.size * metrics.dpi / 72
                SizeUnit.EM_100 -> (length.size * fontsize + 50) / 100
                SizeUnit.REM_100 -> (length.size * metrics.fontSize + 50) / 100
                SizeUnit.EX_100 ->
                    // TODO 0.5 font size => height of x
                    (length.size * fontsize / 2 + 50) / 100
                SizeUnit.PERCENT -> (length.size * getFullSize(metrics, fontsize, featureId) + 50) / 100
                else -> length.size.toInt()
            }
        }
    }

    private var mFeatureMask: Short = 0

    private val mLengths = arrayOfNulls<Length>(Feature.NUMBER_OF_LENGTHS)
    private var mAlignmentType: Byte = 0
    // TODO:暂时不处理字体类型
    // private var mFontEntries: List<FontEntry>? = null
    private var mSupportedFontModifiers: Byte = 0
    private var mFontModifiers: Byte = 0
    private var mVerticalAlignCode: Byte = 0

    fun isFeatureSupported(featureId: Int): Boolean {
        return isFeatureSupported(mFeatureMask, featureId)
    }

    fun setLength(featureId: Int, size: Short, unit: Byte) {
        mFeatureMask = mFeatureMask or (1 shl featureId).toShort()
        mLengths[featureId] = Length(size, unit)
    }

    fun getLength(featureId: Int, metrics: TextMetrics, fontSize: Int): Int {
        val length = mLengths[featureId]
        return if (length == null) {
            0
        } else {
            compute(mLengths[featureId]!!, metrics, fontSize, featureId)
        }
    }

    fun hasNonZeroLength(featureId: Int): Boolean {
        val length = mLengths[featureId]
        return if (length == null) {
            false
        } else {
            mLengths[featureId]!!.size.toInt() != 0
        }
    }

    fun setAlignmentType(alignmentType: Byte) {
        mFeatureMask = mFeatureMask or (1 shl Feature.ALIGNMENT_TYPE).toShort()
        mAlignmentType = alignmentType
    }

    fun getAlignmentType(): Byte {
        return mAlignmentType
    }

/*    fun setFontFamilies(fontManager: FontManager, fontFamiliesIndex: Int) {
        mFeatureMask = mFeatureMask or (1 shl Feature.FONT_FAMILY).toShort()
        mFontEntries = fontManager.getFamilyEntries(fontFamiliesIndex)
    }

    fun getFontEntries(): List<FontEntry> {
        return mFontEntries
    }*/

    fun setFontModifiers(supported: Byte, values: Byte) {
        mFeatureMask = mFeatureMask or (1 shl Feature.FONT_STYLE_MODIFIER).toShort()
        mSupportedFontModifiers = supported
        mFontModifiers = values
    }

    fun setFontModifier(modifier: Byte, on: Boolean) {
        mFeatureMask = mFeatureMask or (1 shl Feature.FONT_STYLE_MODIFIER).toShort()
        mSupportedFontModifiers = mSupportedFontModifiers or modifier
        mFontModifiers = if (on) {
            mFontModifiers or modifier
        } else {
            mFontModifiers and modifier.inv()
        }
    }

    fun getFontModifier(modifier: Byte): Boolean? {
        if (mSupportedFontModifiers and modifier == 0.toByte()) {
            return null
        }
        return mFontModifiers and modifier != 0.toByte()
    }

    fun setVerticalAlignCode(code: Byte) {
        mFeatureMask = mFeatureMask or (1 shl Feature.NON_LENGTH_VERTICAL_ALIGN).toShort()
        mVerticalAlignCode = code
    }

    fun getVerticalAlignCode(): Byte {
        return mVerticalAlignCode
    }

    override fun toString(): String {
        val buffer = StringBuilder("StyleEntry[")
        buffer.append("features: ").append(mFeatureMask.toInt()).append(";")
        if (isFeatureSupported(Feature.LENGTH_SPACE_BEFORE)) {
            buffer.append("space-before: ").append(mLengths[Feature.LENGTH_SPACE_BEFORE]).append(";")
        }
        if (isFeatureSupported(Feature.LENGTH_SPACE_AFTER)) {
            buffer.append("space-after: ").append(mLengths[Feature.LENGTH_SPACE_AFTER]).append(";")
        }
        buffer.append("]")
        return buffer.toString()
    }
}