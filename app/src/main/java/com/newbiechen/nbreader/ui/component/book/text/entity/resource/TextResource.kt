package com.newbiechen.nbreader.ui.component.book.text.entity.resource

import com.newbiechen.nbreader.ui.component.book.text.entity.resource.image.TextImage
import com.newbiechen.nbreader.ui.component.book.text.entity.resource.image.TextImageFactory
import com.newbiechen.nbreader.ui.component.book.text.util.TextByteToBasicUtil
import com.newbiechen.nbreader.uilts.LogHelper

/**
 *  author : newbiechen
 *  date : 2020/2/29 12:12 AM
 *  description :文本资源信息
 */

class TextResource(resourceData: ByteArray?) {

    companion object {
        private const val TAG = "TextResource"

        // 防止存在资源与 id 冲突的情况
        fun getKey(type: Byte, id: String): String {
            return "$type@$id"
        }
    }

    // TODO:存在与其他资源重名的情况，该如何处理
    // 章节中包含的所有标签
    private var mResourceMap: Map<String, Any?> = if (resourceData == null) {
        emptyMap()
    } else {
        TextResourceDecoder(resourceData).decode()
    }

    private fun getValue(type: Byte, id: String): Any? {
        return mResourceMap[getKey(type, id)]
    }

    fun getImage(resourceId: Int): TextImage? {
        return getValue(TextResType.IMAGE, resourceId.toString()) as TextImage
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
    fun decode(): Map<String, Any?> {
        // 重置
        mBufferOffset = 0

        val textAttrMap = mutableMapOf<String, Any?>()
        val bufferLen = resourceData.size

        while (mBufferOffset < bufferLen) {
            // 获取当前索引下的标签类型
            val attrType = readAttrType()

            // 生成对应的 TextTag
            var resourceInfo: Pair<String, Any?>? = null

            when (attrType) {
                TextResType.IMAGE -> {
                    resourceInfo = readImageAttr()
                }
            }

            LogHelper.i(TAG, "decode: $attrType")

            val key = TextResource.getKey(attrType, resourceInfo!!.first)

            textAttrMap[key] = resourceInfo!!.second
        }
        return textAttrMap
    }

    // 读取标签类型
    private fun readAttrType(): Byte {
        // 从缓冲区中获取 tag 的类型
        var tagType = resourceData[mBufferOffset]
        // tag 类型后，是填充对齐类型占 1 字节，所以需要偏移 2 字节。
        mBufferOffset += 2
        return tagType
    }

    private fun readImageAttr(): Pair<String, Any?> {
        // 读取 image 所属的 id
        var tempArr = resourceData.copyOfRange(mBufferOffset, mBufferOffset + 2)
        val id = TextByteToBasicUtil.toUInt16(tempArr)
        mBufferOffset += 2

        // 获取路径
        tempArr = resourceData.copyOfRange(mBufferOffset, mBufferOffset + 2)
        // 文本字节长度
        val textLength = TextByteToBasicUtil.toUInt16(tempArr)
        mBufferOffset += 2

        // 路径信息
        val path = String(resourceData, mBufferOffset, textLength, Charsets.UTF_16LE)
        mBufferOffset += textLength

        return Pair(id.toString(), TextImageFactory.getTextImage(path))
    }
}