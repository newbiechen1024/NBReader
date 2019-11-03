package com.example.newbiechen.nbreader.ui.component.book.text.hyphenation

/**
 *  author : newbiechen
 *  date : 2019-11-03 15:40
 *  description : TeX 排版连字符号匹配器
 */

/**
 * @param pattern:待匹配字符数组
 * @param offset:起始偏移位置
 * @param length:待检测字节的长度
 * @param useValues:是否区分文字和数字
 */
class TextTeXHyphenPattern(pattern: CharArray, offset: Int, length: Int, useValues: Boolean) {

    private var mLength: Int = 0
    private var mSymbols: CharArray
    private var mValues: ByteArray?
    private var mHashCode: Int = 0

    init {
        if (useValues) {
            var patternLength = 0
            // 计算非数字的长度
            for (i in 0 until length) {
                val symbol = pattern[offset + i]
                if (symbol > '9' || symbol < '0') {
                    ++patternLength
                }
            }

            // 非数字标记
            val symbols = CharArray(patternLength)
            // 数字标记
            // TODO:这部分代码很奇怪，获取的是非数字长度的大小
            val values = ByteArray(patternLength + 1)

            var i = 0
            var k = 0

            // 将数字存储到 value 数组中，将非数字存储到 symbol 数组中
            while (i < length) {
                val sym = pattern[offset + i]
                // TODO：但是这里，如果判断是数字，则加入到 value 中。不过只要保证文字 > 数字，则不会崩溃。
                // TODO：暂时不太懂他们为什么这么实现，先直接抄过来吧
                if (sym in '0'..'9') {
                    values[k] = (sym - '0').toByte()
                } else {
                    symbols[k] = sym
                    ++k
                }
                ++i
            }

            mLength = patternLength
            mSymbols = symbols
            mValues = values
        } else {
            val symbols = CharArray(length)
            System.arraycopy(pattern, offset, symbols, 0, length)
            mLength = length
            mSymbols = symbols
            mValues = null
        }
    }

    internal fun update(pattern: CharArray, offset: Int, length: Int) {
        // We assert
        // 1. this pattern doesn't use values ==> 这个匹配器不会区分数字
        // 2. length <= original pattern length ==> 新的长度必须小于初始匹配长度

        System.arraycopy(pattern, offset, mSymbols, 0, length)
        mLength = length
        mHashCode = 0
    }

    internal fun apply(mask: ByteArray, position: Int) {
        val patternLength = mLength
        val values = mValues
        var i = 0
        var j = position
        while (i <= patternLength) {
            val value = values!![i]
            if (mask[j] < value) {
                mask[j] = value
            }
            ++i
            ++j
        }
    }

    internal fun reset(length: Int) {
        mLength = length
        mHashCode = 0
    }

    internal fun length(): Int {
        return mLength
    }

    override fun equals(o: Any?): Boolean {
        val pattern = o as TextTeXHyphenPattern?
        var len = mLength
        if (len != pattern!!.mLength) {
            return false
        }
        val symbols0 = mSymbols
        val symbols1 = pattern!!.mSymbols
        while (len-- != 0) {
            if (symbols0[len] != symbols1[len]) {
                return false
            }
        }
        return true
    }

    override fun hashCode(): Int {
        var hash = mHashCode
        if (hash == 0) {
            val symbols = mSymbols
            hash = 0
            var index = mLength
            while (index-- != 0) {
                hash *= 31
                hash += symbols[index].toInt()
            }
            mHashCode = hash
        }
        return hash
    }

    override fun toString(): String {
        val buffer = StringBuilder()
        for (i in 0 until mLength) {
            if (mValues != null) {
                buffer.append(mValues!![i].toInt())
            }
            buffer.append(mSymbols[i])
        }
        if (mValues != null) {
            buffer.append(mValues!![mLength].toInt())
        }
        return buffer.toString()
    }
}