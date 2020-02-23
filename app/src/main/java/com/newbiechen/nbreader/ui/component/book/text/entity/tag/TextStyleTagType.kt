package com.newbiechen.nbreader.ui.component.book.text.entity.tag

/**
 *  author : newbiechen
 *  date : 2020-02-23 18:56
 *  description :样式标签支持的样式
 */

object TextFeature {
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

object TextFontModifier {
    const val FONT_MODIFIER_BOLD = (1 shl 0).toByte()
    const val FONT_MODIFIER_ITALIC = (1 shl 1).toByte()
    const val FONT_MODIFIER_UNDERLINED = (1 shl 2).toByte()
    const val FONT_MODIFIER_STRIKEDTHROUGH = (1 shl 3).toByte()
    const val FONT_MODIFIER_SMALLCAPS = (1 shl 4).toByte()
    const val FONT_MODIFIER_INHERIT = (1 shl 5).toByte()
    const val FONT_MODIFIER_SMALLER = (1 shl 6).toByte()
    const val FONT_MODIFIER_LARGER = (1 shl 7).toByte()
}

object TextSizeUnit {
    const val PIXEL: Byte = 0
    const val POINT: Byte = 1
    const val EM_100: Byte = 2
    const val REM_100: Byte = 3
    const val EX_100: Byte = 4
    const val PERCENT: Byte = 5 // TODO: add IN, CM, MM, PICA ("pc", = 12 POINT)
}