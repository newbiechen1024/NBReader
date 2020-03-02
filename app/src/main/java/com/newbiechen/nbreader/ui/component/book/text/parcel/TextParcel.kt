package com.newbiechen.nbreader.ui.component.book.text.parcel

import com.newbiechen.nbreader.ui.component.book.text.util.TextByteToBasicUtil

/**
 *  author : newbiechen
 *  date : 2020/3/2 4:37 PM
 *  description :文本包装器
 */

class TextParcel(private val parcelBuffer: ByteArray, startIndex: Int = 0) {
    private var mOffset = startIndex

    companion object {
        private const val TAG = "TextParcel"
    }

    fun readBoolean(): Boolean {
        return readByte() > 0
    }

    fun readByte(): Byte {
        val value = parcelBuffer[mOffset]
        ++mOffset
        return value
    }

    fun readShort(): Short {
        val tempArr = parcelBuffer.copyOfRange(mOffset, mOffset + 2)
        mOffset += 2
        return TextByteToBasicUtil.toShort(tempArr)
    }

    fun readInt(): Int {
        val tempArr = parcelBuffer.copyOfRange(mOffset, mOffset + 4)
        mOffset += 4
        return TextByteToBasicUtil.toInt32(tempArr)
    }

    /**
     * 读取长度为 16 位的字符串
     */
    fun readString16(): String {
        val length = readShort()
        return readStringInternal(length.toInt())
    }

    /**
     * 读取长度为 32 位的字符串
     */
    fun readString32(): String {
        val length = readInt()
        return readStringInternal(length)
    }

    private fun readStringInternal(strLen: Int): String {
        if (strLen == 0) {
            return String()
        }

        val str = String(parcelBuffer, mOffset, strLen)
        mOffset += strLen
        return str
    }

    /**
     * 读取长度为 16 位的字符串数组
     */
    fun readString16Array(): Array<String> {
        // 数组长度
        val arrLen = readInt()
        return Array(arrLen) {
            readString16()
        }
    }

    /**
     * 读取长度为 32 位的字符串数组
     */
    fun readString32Array(): Array<String> {
        // 数组长度
        val arrLen = readInt()
        return Array(arrLen) {
            readString32()
        }
    }

    /**
     * 当前读取的偏移
     */
    fun offset(): Int {
        // 读取的长度
        return mOffset
    }
}