package com.newbiechen.nbreader.ui.component.book.text.entity.tag

import com.newbiechen.nbreader.ui.component.book.text.entity.TextMetrics
import com.newbiechen.nbreader.ui.component.book.text.parcel.TextParcel
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

/**
 *  author : newbiechen
 *  date : 2020-02-23 18:55
 *  description :文本样式标签
 */
open class TextStyleTag(parcel: TextParcel) : TextTag {
    companion object {
        private fun getFullSize(metrics: TextMetrics, fontSize: Int, featureId: Int): Int {
            return when (featureId) {
                TextFeature.LENGTH_MARGIN_LEFT, TextFeature.LENGTH_MARGIN_RIGHT,
                TextFeature.LENGTH_PADDING_LEFT, TextFeature.LENGTH_PADDING_RIGHT,
                TextFeature.LENGTH_FIRST_LINE_INDENT -> metrics.fullWidth

                TextFeature.LENGTH_SPACE_BEFORE, TextFeature.LENGTH_SPACE_AFTER -> metrics.fullHeight

                TextFeature.LENGTH_VERTICAL_ALIGN, TextFeature.LENGTH_FONT_SIZE -> fontSize

                else -> metrics.fullWidth
            }
        }

        fun compute(length: Length, metrics: TextMetrics, fontSize: Int, featureId: Int): Int {
            return when (length.unit) {
                TextSizeUnit.PIXEL -> length.size.toInt()
                TextSizeUnit.POINT -> length.size * metrics.dpi / 72
                TextSizeUnit.EM_100 -> (length.size * fontSize + 50) / 100
                TextSizeUnit.REM_100 -> (length.size * metrics.fontSize + 50) / 100
                TextSizeUnit.EX_100 ->
                    // TODO 0.5 font size => height of x
                    (length.size * fontSize / 2 + 50) / 100
                TextSizeUnit.PERCENT -> (length.size * getFullSize(
                    metrics,
                    fontSize,
                    featureId
                ) + 50) / 100
                else -> length.size.toInt()
            }
        }
    }

    // 样式深度
    private var mDepth: Byte = 0
    // 支持的功能标记
    private var mFeatureMask: Short = 0
    // 支持的字体样式
    private var mSupportedFontModifiers: Byte = 0

    private val mLengths = arrayOfNulls<Length>(TextFeature.NUMBER_OF_LENGTHS)
    // 对齐方式
    private var mAlignmentType: Byte = 0
    // TODO:暂时不处理字体类型
    // private var mFontEntries: List<FontEntry>? = null
    //
    // 当前的字体样式
    private var mFontModifiers: Byte = 0
    // 竖直对齐
    private var mVerticalAlignCode: Byte = 0

    init {
        initStyle(parcel)
    }

    private fun initStyle(parcel: TextParcel) {
        // 深度
        mDepth = parcel.readByte()
        // 支持的功能标记
        mFeatureMask = parcel.readShort()

        // 处理长度相关的样式
        for (i in 0 until TextFeature.NUMBER_OF_LENGTHS) {
            if (isFeatureSupported(i)) {
                val size = parcel.readShort()
                val unit = parcel.readByte()
                mLengths[i] = Length(size, unit)
            }
        }

        // 对齐功能
        if (isFeatureSupported(TextFeature.ALIGNMENT_TYPE)) {
            mAlignmentType = parcel.readByte()
        }

        if (isFeatureSupported(TextFeature.NON_LENGTH_VERTICAL_ALIGN)) {
            mVerticalAlignCode = parcel.readByte()
        }

        // 字体集
        if (isFeatureSupported(TextFeature.FONT_FAMILY)) {
            // 未处理
        }

        // 支持的字体样式：如 bold、itatic 之类的
        if (isFeatureSupported(TextFeature.FONT_STYLE_MODIFIER)) {
            // 未处理
        }
    }

    fun isFeatureSupported(featureId: Int): Boolean {
        return mFeatureMask.toInt() and (1 shl featureId) != 0
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

    fun getAlignmentType(): Byte {
        return mAlignmentType
    }

    fun getDepth() = mDepth

/*    fun setFontFamilies(fontManager: FontManager, fontFamiliesIndex: Int) {
        mFeatureMask = mFeatureMask or (1 shl Feature.FONT_FAMILY).toShort()
        mFontEntries = fontManager.getFamilyEntries(fontFamiliesIndex)
    }

    fun getFontEntries(): List<FontEntry> {
        return mFontEntries
    }*/

    fun getFontModifier(modifier: Byte): Boolean? {
        if (mSupportedFontModifiers and modifier == 0.toByte()) {
            return null
        }
        return mFontModifiers and modifier != 0.toByte()
    }

    fun getVerticalAlignCode(): Byte {
        return mVerticalAlignCode
    }

    override fun toString(): String {
        val buffer = StringBuilder("StyleEntry[")
        buffer.append("features: ").append(mFeatureMask.toInt()).append(";")
        if (isFeatureSupported(TextFeature.LENGTH_SPACE_BEFORE)) {
            buffer.append("space-before: ").append(mLengths[TextFeature.LENGTH_SPACE_BEFORE])
                .append(";")
        }
        if (isFeatureSupported(TextFeature.LENGTH_SPACE_AFTER)) {
            buffer.append("space-after: ").append(mLengths[TextFeature.LENGTH_SPACE_AFTER])
                .append(";")
        }
        buffer.append("]")
        return buffer.toString()
    }

    /**
     * 带单位的长度
     */
    data class Length(val size: Short, val unit: Byte) {
        override fun toString(): String {
            return "$size.$unit"
        }
    }
}

// 关于 css 的样式标签
class TextCssStyleTag(parcel: TextParcel) : TextStyleTag(parcel)

// 关于其他类型的样式标签
class TextOtherStyleTag(parcel: TextParcel) : TextStyleTag(parcel)