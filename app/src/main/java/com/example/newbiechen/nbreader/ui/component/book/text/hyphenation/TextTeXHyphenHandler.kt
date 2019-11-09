package com.example.newbiechen.nbreader.ui.component.book.text.hyphenation

import android.text.TextUtils
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

/**
 *  author : newbiechen
 *  date : 2019-11-03 16:05
 *  description:TeX Hyphen XML 解析器
 */

class TextTeXHyphenHandler(private val hyphenator: TextTeXHyphenator) : DefaultHandler() {

    companion object {
        private const val TAG_PATTERN = "pattern"

    }

    private var isReadPattern: Boolean = false
    private var mBuffer = CharArray(10)
    private var mBufferLength: Int = 0

    override fun startElement(
        uri: String?,
        localName: String?,
        qName: String?,
        attributes: Attributes?
    ) {
        if (TAG_PATTERN == localName) {
            isReadPattern = true
        }
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {
        if (TAG_PATTERN == localName) {
            val len = mBufferLength
            isReadPattern = false
            if (len != 0) {
                // 添加断点匹配模式
                hyphenator.addPattern(TextTeXHyphenPattern(mBuffer, 0, len, true))
            }
            mBufferLength = 0
        }
    }

    override fun characters(ch: CharArray?, start: Int, length: Int) {
        if (isReadPattern && ch != null) {
            val oldLen = mBufferLength
            val newLen = oldLen + length
            // 判断新的长度是否大于当前缓冲区
            if (newLen > mBuffer.size) {
                // 增加缓冲区的大小
                mBuffer = mBuffer.copyOf(newLen + 10)
            }
            // 将数据存储到 buffer 中
            System.arraycopy(ch, start, mBuffer, oldLen, length)
            mBufferLength = newLen
        }
    }
}

