package com.example.newbiechen.nbreader.ui.component.book.text.util

import okhttp3.internal.and
import java.nio.ByteBuffer


/**
 *  author : newbiechen
 *  date : 2020-01-12 15:18
 *  description :将 byte 数据转换成基础类型数据
 */

object ByteToDataUtil {

    fun readUInt32(byteArr: ByteArray): Long {
        return ByteBuffer.allocate(8)
            .put(byteArrayOf(0, 0, 0, 0))
            .put(byteArr)
            .long
    }

    fun readBoolean(byte: Byte): Boolean {
        return (byte and 0x01) == 1
    }
}