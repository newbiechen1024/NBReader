package com.newbiechen.nbreader.ui.component.book.text.hyphenation

import android.content.Context
import com.newbiechen.nbreader.ui.component.book.text.entity.element.TextWordElement
import com.newbiechen.nbreader.ui.component.book.text.language.ExtLanguage
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.util.*
import javax.xml.parsers.SAXParserFactory

/**
 *  author : newbiechen
 *  date : 2019-11-03 15:33
 *  description :基于 TeX 排版的断句处理器
 */

class TextTeXHyphenator private constructor() {
    // 由于 TextTeXHyphenPattern 自身实现了 hashCode，所以并不是对象实例的上的匹配
    // 缓存匹配器
    private val mPatternTable = HashMap<TextTeXHyphenPattern, TextTeXHyphenPattern>()
    private var mMaxPatternLength: Int = 0
    private var mLanguage: String? = null

    companion object {
        var sInstance: TextTeXHyphenator? = null

        fun getInstance(): TextTeXHyphenator {
            if (sInstance == null) {
                sInstance = TextTeXHyphenator()
            }
            return sInstance!!
        }
    }

    fun addPattern(pattern: TextTeXHyphenPattern) {
        mPatternTable[pattern] = pattern
        if (mMaxPatternLength < pattern.length()) {
            mMaxPatternLength = pattern.length()
        }
    }

    // TODO:如果存在没有对应 language 对应的 pattern 资源文件的情况该怎么办？
    fun load(context: Context, language: String?) {
        var lang = language
        if (lang == null || ExtLanguage.OTHER_CODE == lang) {
            lang = Locale.getDefault().language
        }

        if (lang == null || lang == mLanguage) {
            return
        }

        mLanguage = lang

        clear()

        // 创建 sax 解析器
        val saxParserFactory = SAXParserFactory.newInstance()
        val saxParser = saxParserFactory.newSAXParser()
        // 创建 Hyphenator 解析器
        val hyphenHandler = TextTeXHyphenHandler(this)
        var resourceInputStream: InputStream? = null
        try {
            resourceInputStream = context.assets.open("hyphenationPatterns/$lang.pattern")
            saxParser.parse(resourceInputStream, hyphenHandler)
        } catch (exception: IOException) {
            // 可能存在资源文件不存在的情况
        } finally {
            try {
                resourceInputStream?.close()
            } catch (e: Exception) {
            }
        }
    }

    fun clear() {
        mPatternTable.clear()
        mMaxPatternLength = 0
    }

    /**
     * 进行断句操作
     */
    fun hyphenate(stringToHyphenate: CharArray, mask: BooleanArray, length: Int) {
        if (mPatternTable.isEmpty()) {
            for (i in 0 until length - 1) {
                mask[i] = false
            }
            return
        }

        val values = ByteArray(length + 1)

        val table = mPatternTable
        val pattern = TextTeXHyphenPattern(stringToHyphenate, 0, length, false)
        for (offset in 0 until length - 1) {
            var len = (length - offset).coerceAtMost(mMaxPatternLength) + 1
            pattern.update(stringToHyphenate, offset, len - 1)
            while (--len > 0) {
                pattern.reset(len)
                val toApply = table[pattern] as TextTeXHyphenPattern
                toApply.apply(values, offset)
            }
        }

        for (i in 0 until length - 1) {
            mask[i] = values[i + 1] % 2 == 1
        }
    }

    /**
     * 根据 word 获取断句信息
     */
    fun getHyphenInfo(word: TextWordElement): TextHyphenInfo {
        val len = word.length
        val isLetter = BooleanArray(len)
        val pattern = CharArray(len + 2)
        val data = word.data
        pattern[0] = ' '
        run {
            var i = 0
            var j = word.offset
            while (i < len) {
                val character = data[j]
                if (character == '\'' || character == '^' || Character.isLetter(character)) {
                    isLetter[i] = true
                    pattern[i + 1] = Character.toLowerCase(character)
                } else {
                    pattern[i + 1] = ' '
                }
                ++i
                ++j
            }
        }
        pattern[len + 1] = ' '

        val info = TextHyphenInfo(len + 2)
        val mask = info.mask
        hyphenate(pattern, mask, len + 2)
        var i = 0
        var j = word.offset - 1
        while (i <= len) {
            if (i < 2 || i > len - 2) {
                mask[i] = false
            } else {
                when (data[j]) {
                    0xAD.toChar() // soft hyphen
                    -> mask[i] = true
                    '-' -> mask[i] = (i >= 3
                            && isLetter[i - 3]
                            && isLetter[i - 2]
                            && isLetter[i]
                            && isLetter[i + 1])
                    else -> mask[i] = (mask[i]
                            && isLetter[i - 2]
                            && isLetter[i - 1]
                            && isLetter[i]
                            && isLetter[i + 1])
                }
            }
            ++i
            ++j
        }

        return info
    }
}