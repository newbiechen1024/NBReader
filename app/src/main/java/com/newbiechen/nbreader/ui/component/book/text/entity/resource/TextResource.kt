package com.newbiechen.nbreader.ui.component.book.text.entity.resource

import com.newbiechen.nbreader.ui.component.book.text.entity.resource.image.TextImage
import com.newbiechen.nbreader.ui.component.book.text.entity.resource.image.TextImageFactory
import com.newbiechen.nbreader.ui.component.book.text.parcel.TextParcel
import com.newbiechen.nbreader.uilts.LogHelper

/**
 *  author : newbiechen
 *  date : 2020/2/29 12:12 AM
 *  description :文本资源信息
 */

class TextResource(resourceData: ByteArray?) {

    companion object {
        private const val TAG = "TextResource"
    }

    // TODO:存在与其他资源重名的情况，该如何处理
    // 章节中包含的所有标签
    private var mResourceMap: Map<String, Any?> = if (resourceData == null) {
        emptyMap()
    } else {
        TextResourceDecoder(resourceData).decode()
    }

    fun getImage(resourceId: String): TextImage? {
        return mResourceMap[resourceId] as TextImage
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
private class TextResourceDecoder(resourceData: ByteArray) {

    companion object {
        private const val TAG = "TextResourceDecoder"
    }

    private var mParcel: TextParcel = TextParcel(resourceData)
    private var mBufferLen = resourceData.size

    /**
     * 进行解析操作
     */
    fun decode(): Map<String, Any?> {
        val textAttrMap = mutableMapOf<String, Any?>()

        while (mParcel.offset() < mBufferLen) {
            // 获取当前索引下的标签类型
            val attrType = mParcel.readByte()

            // 生成对应的 TextTag
            var resourceInfo: Pair<String, Any?>? = null

            when (attrType) {
                TextResType.IMAGE -> {
                    resourceInfo = readImageAttr()
                }
            }

            LogHelper.i(TAG, "decode: $attrType")

            textAttrMap[resourceInfo!!.first] = resourceInfo!!.second
        }
        return textAttrMap
    }

    private fun readImageAttr(): Pair<String, Any?> {
        val imageAttr = TextImageAttr(mParcel)
        return Pair(imageAttr.id, TextImageFactory.getTextImage(imageAttr.path))
    }
}