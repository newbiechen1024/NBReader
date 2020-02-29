package com.newbiechen.nbreader.ui.component.book.text.entity.resource

import com.newbiechen.nbreader.uilts.ByteToBasicUtil
import com.newbiechen.nbreader.uilts.LogHelper

/**
 *  author : newbiechen
 *  date : 2020/2/29 12:12 AM
 *  description :文本资源信息
 */

class TextResource(private val resourceData: ByteArray?) {
    companion object {
        private const val TAG = "TextResource"
    }
    // TODO:转化成 map 会不会更好？，但是每个参数的 id 都是不一样的。还是说 id 都用文本表示？(暂时先不管了)

    // 章节中包含的所有标签
    private var mTextAttrList: List<TextAttribute> = if (resourceData == null) {
        emptyList()
    } else {
        TextResourceDecoder(resourceData).decode()
    }

    init {
        mTextAttrList.forEach {
            LogHelper.i(TAG, "init: $it")
        }
    }

    fun getImage(resourceId: Int) {
        // 查找
    }

    fun getFont(resourceId: Int) {

    }

    // 获取 id 对应的 paragraph 索引
    fun getParagraphIndexById() {

    }

    // 文本资源解析器
}

/**
 * 文本资源解析器
 */
private class TextResourceDecoder(private val resourceData: ByteArray) {
    // 缓存区的偏移
    private var mBufferOffset = 0

    companion object {
        private const val TAG = "TextResourceDecoder"
    }

    /**
     * 进行解析操作
     */
    fun decode(): ArrayList<TextAttribute> {
        // 重置
        mBufferOffset = 0

        val textTags = ArrayList<TextAttribute>()

        val bufferLen = resourceData.size

        LogHelper.i(TAG, "decode: size = ${resourceData.size}")

        while (mBufferOffset < bufferLen) {
            // 获取当前索引下的标签类型
            val tagType = readAttrType()

            // 生成对应的 TextTag
            var textAttr: TextAttribute? = null

            when (tagType) {
                TextResType.IMAGE -> {
                    textAttr = readImageAttr()
                }
            }

            LogHelper.i(TAG, "decode: $tagType")
            textTags.add(textAttr!!)
        }

        return textTags
    }

    // 读取标签类型
    private fun readAttrType(): Byte {
        // 从缓冲区中获取 tag 的类型
        var tagType = resourceData[mBufferOffset]
        // tag 类型后，是填充对齐类型占 1 字节，所以需要偏移 2 字节。
        mBufferOffset += 2
        return tagType
    }

    private fun readImageAttr(): TextAttribute {
        // 读取 image 所属的 id
        var tempArr = resourceData.copyOfRange(mBufferOffset, mBufferOffset + 2)
        val id = ByteToBasicUtil.toUInt16(tempArr)
        mBufferOffset += 2

        // 获取路径
        tempArr = resourceData.copyOfRange(mBufferOffset, mBufferOffset + 2)
        // 文本字节长度
        val textLength = ByteToBasicUtil.toUInt16(tempArr)
        mBufferOffset += 2

        // 路径信息
        val path = String(resourceData, mBufferOffset, textLength, Charsets.UTF_16LE)
        mBufferOffset += textLength

        return TextImageAttr(id, path)
    }
}