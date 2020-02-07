package com.newbiechen.nbreader.ui.component.book.text.util

/**
 *  author : newbiechen
 *  date : 2019-10-21 19:50
 *  description :将一行文本数据，分割成单词。
 */

object LineBreaker {
    init {
        init()
    }

    // BREAK 生成的类型
    const val MUSTBREAK: Byte = 0
    const val ALLOWBREAK: Byte = 1
    const val NOBREAK: Byte = 2
    const val INSIDEACHAR: Byte = 3

    private external fun init()

    private external fun setLineBreakForCharArray(
        textData: CharArray, offset: Int,
        length: Int, lang: String, bytes: ByteArray
    )


    private external fun setLineBreakForString(
        text: String, lang: String, bytes: ByteArray
    )

    fun setLineBreak(
        textData: CharArray, offset: Int,
        length: Int, lang: String, bytes: ByteArray
    ) {
        setLineBreakForCharArray(
            textData,
            offset,
            length,
            lang,
            bytes
        )
    }

    fun setLineBreak(text: String, lang: String, bytes: ByteArray) {
        setLineBreakForString(
            text,
            lang,
            bytes
        )
    }
}