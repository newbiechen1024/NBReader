package com.example.newbiechen.nbreader.ui.component.book.text

import com.example.newbiechen.nbreader.ui.component.book.text.entity.entry.TextEntry
import com.example.newbiechen.nbreader.ui.component.book.text.entity.entry.TextParagraphEntry
import com.example.newbiechen.nbreader.ui.component.book.text.entity.entry.TextParagraphEntryType
import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextParagraphInfo
import com.example.newbiechen.nbreader.ui.component.book.text.entity.entry.TextControlEntry
import com.example.newbiechen.nbreader.ui.component.book.text.util.TextCacheReader
import com.example.newbiechen.nbreader.uilts.LogHelper
import kotlin.experimental.and
import kotlin.math.min

/**
 *  author : newbiechen
 *  date : 2019-10-14 20:03
 *  description :存储文本的信息模块
 */

class TextPlainModel : TextModel {

    companion object {
        private const val TAG = "TextPlainModel"
    }

    private val mId: String?
    private val mLang: String
    private val mBlockCount: Int
    private val mCacheDir: String
    private val mFileExtension: String
    private val mPghInfoList: Array<TextParagraphInfo>
    private val mCacheReader: TextCacheReader

    constructor(
        // 文本信息
        id: String?,
        lang: String,
        // 缓冲文件信息
        bufferBlockCount: Int,
        cacheDir: String,
        fileExtension: String,
        // 每个段落的数据信息
        paragraphInfos: Array<TextParagraphInfo>
    ) {
        mId = id
        mLang = lang
        mBlockCount = bufferBlockCount
        mCacheDir = cacheDir
        mFileExtension = fileExtension
        mPghInfoList = paragraphInfos

        // 创建缓冲块读取器
        mCacheReader = TextCacheReader(
            cacheDir,
            fileExtension,
            bufferBlockCount
        )
    }

    override fun getId(): String? {
        return mId
    }

    override fun getLanguage(): String {
        return mLang
    }

    override fun getParagraphCount(): Int {
        return mPghInfoList.size
    }

    override fun getTextLength(index: Int): Int {
        return if (index >= getParagraphCount()) {
            0
        } else {
            mPghInfoList[index].textLength
        }
    }

    override fun getParagraph(index: Int): TextParagraph? {
        if (index >= getParagraphCount()) {
            return null
        }

        return TextParagraphImpl(index)
    }

    // TextParagraph Entry 遍历器
    inner class EntryIteratorImpl(index: Int) : TextParagraph.EntryIterator {

        private var mParagraphInfo: TextParagraphInfo = mPghInfoList[index]
        // entry 计数器
        private var mEntryIndex: Int = 0
        // 段落起始位置在 block 中的偏移
        private var mBlockOffset = mParagraphInfo.bufferBlockOffset
        // 段落在第几个缓冲 block 上
        private var mBlockIndex = mParagraphInfo.bufferBlockIndex

        override fun hasNext(): Boolean {
            return mEntryIndex < mParagraphInfo.entryCount
        }

        override fun next(): TextParagraphEntry? {
            if (!hasNext()) {
                // 抛出异常
                return null
            }

            var blockOffset = mBlockOffset

            LogHelper.i(TAG, "next: get cur block data")

            // 获取段落所在的缓冲块
            var blockData = mCacheReader.getBufferBlock(mBlockIndex)
            if (blockData.isEmpty()) {
                // 抛出异常
                return null
            }

            // 如果 offset 超出当前 block ，则获取下一个 block
            if (blockOffset >= blockData.size) {
                blockData = mCacheReader.getBufferBlock(++mBlockIndex)
                if (blockData.isEmpty()) {
                    return null
                }
                // 重新初始化 offset 偏移
                blockOffset = 0
            }

            var first = blockData[blockOffset].toShort()
            var type = first.toByte()
            if (type.toInt() == 0) {
                blockData = mCacheReader.getBufferBlock(++mBlockIndex)
                if (blockData.isEmpty()) {
                    return null
                }
                blockOffset = 0

                first = blockData[0].toShort()
                type = first.toByte()
            }

            ++blockOffset

            var textEntry: TextParagraphEntry? = null

            when (type) {
                TextParagraphEntryType.TEXT -> {
                    var textLength = blockData[blockOffset++].toInt()

                    textLength += blockData[blockOffset++].toInt() shl 16
                    textLength = min(textLength, blockData.size - blockOffset)

                    textEntry = TextEntry(
                        blockData,
                        blockOffset,
                        textLength
                    )

                    // 读取文本数据，并对 block 进行偏移
                    blockOffset += textLength

                }
                TextParagraphEntryType.CONTROL -> {
                    val type = blockData[blockOffset++].toShort()
                    val controlType = type.toByte()
                    var isControlStart = (controlType and 0x0100.toByte()) == 0x0100.toByte()
                    textEntry = TextControlEntry(controlType, isControlStart)
                }
            }

            // 更新偏移信息
            ++mEntryIndex
            mBlockOffset = blockOffset

            return textEntry
        }

        override fun reset() {
            mEntryIndex = 0
            mBlockOffset = mParagraphInfo.bufferBlockOffset
            mBlockIndex = mParagraphInfo.bufferBlockIndex
        }
    }

    // 段落结构信息
    inner class TextParagraphImpl(private val index: Int) : TextParagraph {

        override fun getInfo(): TextParagraphInfo {
            return mPghInfoList[index]
        }

        override fun getIterator(): TextParagraph.EntryIterator {
            return EntryIteratorImpl(index)
        }
    }
}