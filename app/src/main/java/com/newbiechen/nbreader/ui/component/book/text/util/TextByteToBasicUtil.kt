package com.newbiechen.nbreader.ui.component.book.text.util

import okhttp3.internal.and
import java.nio.ByteBuffer


/**
 *  author : newbiechen
 *  date : 2020-01-12 15:18
 *  description :将 byte 数据转换成基础类型数据
 */

object TextByteToBasicUtil {
    fun toShort(byteArr: ByteArray): Short {
        val byteBuffer = ByteBuffer.allocate(2)
            .put(byteArr)
        byteBuffer.position(0)
        return byteBuffer.short
    }

    fun toUInt16(byteArr: ByteArray): Int {
        val byteBuffer = ByteBuffer.allocate(4)
            .put(byteArrayOf(0, 0))
            .put(byteArr)
        byteBuffer.position(0)
        return byteBuffer.int
    }

    fun toInt32(byteArr: ByteArray): Int {
        val byteBuffer = ByteBuffer.allocate(4)
            .put(byteArr)
        byteBuffer.position(0)
        return byteBuffer.int
    }

    fun toUInt32(byteArr: ByteArray): Long {
        val byteBuffer = ByteBuffer.allocate(8)
            .put(byteArrayOf(0, 0, 0, 0))
            .put(byteArr)

        byteBuffer.position(0)

        return byteBuffer.long
    }

    fun toBoolean(byte: Byte): Boolean {
        return (byte and 0x01) == 1
    }
}