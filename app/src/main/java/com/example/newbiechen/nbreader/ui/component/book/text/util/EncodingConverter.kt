package com.example.newbiechen.nbreader.ui.component.book.text.util

import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.nio.charset.CharsetDecoder
import java.nio.charset.CodingErrorAction

/**
 *  author : newbiechen
 *  date : 2019-11-24 00:15
 *  description :字符集编码转换器
 *  TODO：用于给 Native 层调用
 */

class EncodingConverter private constructor(encoding: String) {
    val name: String = encoding

    companion object {

        // kotlin 编译成 java 代码时，static 方法会在 EncodingConverter 生成 Companion 这个内部静态类。
        // 导致 Native 层索引 static 方法有问题，所以必须使用 JvmStatic 标记。
        @JvmStatic
        fun createEncodingConverter(encoding: String): EncodingConverter? {
            return if (isEncodingSupport(encoding)) EncodingConverter(encoding) else null
        }

        @JvmStatic
        fun isEncodingSupport(encoding: String): Boolean {
            return try {
                Charset.forName(encoding) != null
            } catch (e: Exception) {
                false
            }
        }
    }

    private var mDecoder: CharsetDecoder = Charset.forName(encoding).newDecoder()
        .onMalformedInput(CodingErrorAction.REPLACE)
        .onUnmappableCharacter(CodingErrorAction.REPLACE)


    fun convert(inBuffer: ByteArray, inOffset: Int, inLength: Int, outBuffer: CharArray): Int {
        val inBuffer = ByteBuffer.wrap(inBuffer, inOffset, inLength)
        val outBuffer = CharBuffer.wrap(outBuffer, 0, outBuffer.size)
        // 解析数据生成 Unicode 字符集
        mDecoder.decode(inBuffer, outBuffer, false)
        return outBuffer.position()
    }

    fun reset() {
        mDecoder.reset()
    }
}