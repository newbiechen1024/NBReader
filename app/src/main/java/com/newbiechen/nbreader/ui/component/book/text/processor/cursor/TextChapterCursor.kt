package com.newbiechen.nbreader.ui.component.book.text.processor.cursor

import android.util.LruCache
import com.newbiechen.nbreader.ui.component.book.text.entity.TextParagraph
import com.newbiechen.nbreader.ui.component.book.text.entity.resource.TextResource
import com.newbiechen.nbreader.ui.component.book.text.entity.tag.*
import com.newbiechen.nbreader.ui.component.book.text.processor.TextModel
import com.newbiechen.nbreader.ui.component.book.text.util.TextByteToBasicUtil
import com.newbiechen.nbreader.ui.component.widget.page.PageType
import com.newbiechen.nbreader.uilts.LogHelper

/**
 *  author : newbiechen
 *  date : 2020-01-05 16:26
 *  description:章节光标
 */

class TextChapterCursor(private val textModel: TextModel, private val chapterIndex: Int) {

    // 章节中的段落信息列表
    private var mTextParagraphList: ArrayList<TextParagraph> = ArrayList()

    // 章节资源信息
    private var mTextResource: TextResource

    // 段落光标缓存 (200 最大值是自定义，可根据情况修改)
    private var mTextParagraphCursorCache: LruCache<Int, TextParagraphCursor?> = LruCache(200)

    // 章节中包含的所有标签
    private var mTextTagList: ArrayList<TextTag>? = null

    // 章节信息
    private var mChapter = textModel.getChapter(chapterIndex)

    companion object {
        private const val TAG = "TextChapterCursor"
    }

    init {
        // TODO:关于获取不到 content 的错误处理，之后再说
        // 文本内容信息
        val textContent = textModel.getChapterContent(chapterIndex)!!

        // 获取文本资源信息
        mTextResource = TextResource(textContent.resourceData)

        // 创建解析器解析
        mTextTagList = TextContentDecoder(
            mTextResource, textContent.contentData
        ).decode()

        // TODO:原理是 paragraph tag 一定是在段落的末尾的。之后会修改 native 将 paragraph 放在起始位置
        // 解析成功后，取出 ParagraphTag 转换成 TextParagraph
        mTextTagList!!.forEachIndexed { index, textTag ->

            // 如果不是 paragraph 类型，直接返回 true
            if (textTag !is TextParagraphTag) {
                return@forEachIndexed
            }

            var lastTextParagraph: TextParagraph? = null

            if (mTextParagraphList.isNotEmpty()) {
                lastTextParagraph = mTextParagraphList.last()
            }

            mTextParagraphList.add(
                TextParagraph(
                    textTag.type,
                    mTextParagraphList.size,
                    lastTextParagraph?.endOffset ?: 0,
                    index
                )
            )
        }
    }

    /**
     * 获取章节索引
     */
    fun getChapterIndex() = chapterIndex

    /**
     * 获取章节
     */
    fun getChapter() = mChapter

    /**
     * 获取上一个章节光标
     */
    fun prevCursor(): TextChapterCursor? {
        return if (hasChapter(PageType.PREVIOUS)) {
            textModel.getChapterCursor(chapterIndex - 1)
        } else {
            null
        }
    }

    fun hasChapter(type: PageType): Boolean {
        return when (type) {
            PageType.PREVIOUS -> {
                chapterIndex - 1 >= 0
            }
            PageType.NEXT -> {
                chapterIndex + 1 < textModel.getChapterCount()
            }
            PageType.CURRENT -> {
                true
            }
        }
    }

    /**
     * 获取下一个章节光标
     */
    fun nextCursor(): TextChapterCursor? {
        return if (hasChapter(PageType.NEXT)) {
            textModel.getChapterCursor(chapterIndex + 1)
        } else {
            null
        }
    }

    /**
     * 是否是第一章
     */
    fun isFirstChapter(): Boolean {
        return chapterIndex == 0
    }

    /**
     * 是否是最后一章
     */
    fun isLastChapter(): Boolean {
        return chapterIndex == (textModel.getChapterCount() - 1)
    }

    /**
     * 获取章节中段落总数
     */
    fun getParagraphCount(): Int = mTextParagraphList.size

    /**
     * 获取章节中具体段落信息
     */
    fun getParagraph(index: Int): TextParagraph {
        if (index >= getParagraphCount()) {
            throw IndexOutOfBoundsException("paragraph index out of chapter paragraph count")
        }
        return mTextParagraphList[index]
    }

    /**
     * 获取段落内容
     */
    fun getParagraphContent(index: Int): TextTagIterator {
        return TextTagIteratorImpl(index)
    }

    /**
     * 获取章节中段落光标索引
     */
    fun getParagraphCursor(index: Int): TextParagraphCursor {
        if (index >= getParagraphCount()) {
            throw IndexOutOfBoundsException("paragraph index out of chapter paragraph count")
        }

        var textParagraphCursor = mTextParagraphCursorCache.get(index)

        if (textParagraphCursor == null) {
            textParagraphCursor = TextParagraphCursor(this, index)
            mTextParagraphCursorCache.put(index, textParagraphCursor)
        }

        return textParagraphCursor
    }

    fun getChapterModel() = textModel


    inner class TextTagIteratorImpl(
        paragraphIndex: Int
    ) : TextTagIterator {

        private val paragraph = getParagraph(paragraphIndex)
        private val endOffset = paragraph.endOffset

        private var curOffset = paragraph.startOffset

        override fun hasNext(): Boolean {
            return curOffset < endOffset
        }

        override fun next(): TextTag {
            return mTextTagList!![curOffset++]
        }

        override fun reset() {
            curOffset = paragraph.startOffset
        }
    }
}

/**
 * 文本内容解析器
 */
private class TextContentDecoder(
    private val textResource: TextResource,
    private val contentData: ByteArray
) {

    // 缓存区的偏移
    private var mBufferOffset = 0

    companion object {
        private const val TAG = "ChapterContentDecoder"
    }

    /**
     * 进行解析操作
     */
    fun decode(): ArrayList<TextTag> {
        // 重置
        mBufferOffset = 0

        val textTags = ArrayList<TextTag>()

        val bufferLen = contentData.size

        while (mBufferOffset < bufferLen) {

            // 获取当前索引下的标签类型
            val tagType = readTagType()

            // 生成对应的 TextTag
            var textTag: TextTag? = null

            when (tagType) {
                TextTagType.TEXT -> {
                    textTag = readContentTag()
                }

                TextTagType.CONTROL -> {
                    textTag = readControlTag()
                }
                TextTagType.PARAGRAPH -> {
                    // 处理段落标签
                    textTag = readParagraphTag()
                }
                TextTagType.STYLE_CSS,
                TextTagType.STYLE_OTHER -> {
                    textTag = readStyleTag(tagType)
                }
                TextTagType.STYLE_CLOSE -> {
                    textTag = readStyleCloseTag()
                }
                TextTagType.FIXED_HSPACE -> {
                    textTag = readFixedHSpaceTag()
                }
                TextTagType.IMAGE -> {
                    // TODO:未实现占位
                    textTag = readImageTag()
                }
                TextTagType.HYPERLINK_CONTROL -> {
                    // TODO:未实现占位
                    textTag = readHyperlinkControlTag()
                }
            }
            LogHelper.i(TAG, "decode: $tagType")

            textTags.add(textTag!!)
        }

        return textTags
    }

    // 读取标签类型
    private fun readTagType(): Byte {
        // 从缓冲区中获取 tag 的类型
        var tagType = contentData[mBufferOffset]
        // tag 类型后，是填充对齐类型占 1 字节，所以需要偏移 2 字节。
        mBufferOffset += 2
        return tagType
    }

    /**
     *  如果标签类型是文本类型
     * TEXT_TAG：占用 (6 + 文本字节数) 格式为 | tag 类型 | 未知类型 | 文本字节长度 | 文本内容
     *
     * 1. tag 类型：占用 1 字节。(native 中 char 类型)
     * 2. 未知类型：占用 1 字节 ==> 基本上为 0 好像没用过 (native 中 char 类型)
     * 3. 文本字节长度：占用 4 字节。(native 中 uint 类型)
     * 4. 文本内容：具体文本内容。(当前采用 utf-16 编码)
     */
    private fun readContentTag(): TextContentTag {
        // 获取文本长度字节数组
        val textLengthArr = contentData.copyOfRange(mBufferOffset, mBufferOffset + 4)

        // 进行偏移操作
        mBufferOffset += 4

        // 文本字节长度
        var textLength = TextByteToBasicUtil.toUInt32(textLengthArr)

        // 文本内容
        val textContent =
            String(contentData, mBufferOffset, textLength.toInt(), Charsets.UTF_16LE)

        // 读取文本数据，并对 block 进行偏移
        mBufferOffset += textLength.toInt()

        return TextContentTag(textContent)
    }

    /**
     * control tag 结构：占用 4 字节，格式为 | tag 类型 | 0 | 控制标签 | 是开放标签还是闭合标签 |
     *
     * 1. tag 类型：占用 1 字节
     * 2. 未知类型：占用 1 字节 ==> 基本上为 0 好像没用过
     * 3. 控制位类型：占用 1 字节 ==> 详见 TextParagraph::Type
     * 4. 标签类型：占用 1 字节 ==> 0 或者是 1
     */
    private fun readControlTag(): TextControlTag {
        // 获取 control 类型，并进行偏移
        val controlType = contentData[mBufferOffset++]
        // 判断是起始标签，还是结尾标签
        var isControlStart = TextByteToBasicUtil.toBoolean(contentData[mBufferOffset++])

        return TextControlTag(controlType, isControlStart)
    }

    /**
     * paragraph tag 结构：占用 4 字节，格式为 | tag 类型 | 0 | 段落标签 | 0
     * 1. tag 类型：占用 1 字节。
     * 2. 未知类型：占用 1 字节。 ==> 基本上为 0 好像没用过
     * 3. 段落类型：占用 1 字节。 ==> 详见 TextParagraph::Type
     * 4. 填充对齐：占用 1 字节。
     */
    private fun readParagraphTag(): TextParagraphTag {
        // 获取 control 类型，并进行偏移
        val paragraphType = contentData[mBufferOffset]
        // 偏移操作
        mBufferOffset += 2
        return TextParagraphTag(paragraphType)
    }

    private fun readStyleTag(type: Byte): TextStyleTag {
        // 获取深度
        val depth = contentData[mBufferOffset]
        var tempArr: ByteArray
        mBufferOffset += 2

        // 创建样式标签
        val styleTag =
            if (type == TextTagType.STYLE_CSS) TextCssStyleTag(depth) else TextOtherStyleTag()
        // 读取 style 使用的样式信息标记
        tempArr = contentData.copyOfRange(mBufferOffset, mBufferOffset + 2)
        val featureMask = TextByteToBasicUtil.toShort(tempArr)
        mBufferOffset += 2

        // 处理长度相关的样式
        for (i in 0 until TextFeature.NUMBER_OF_LENGTHS) {
            if (TextStyleTag.isFeatureSupported(featureMask, i)) {
                tempArr = contentData.copyOfRange(mBufferOffset, mBufferOffset + 2)
                val size = TextByteToBasicUtil.toShort(tempArr)
                mBufferOffset += 2

                val unit = contentData[mBufferOffset]
                mBufferOffset += 2

                styleTag.setLength(i, size, unit)
            }
        }

        // 处理其他功能的样式
        if (TextStyleTag.isFeatureSupported(featureMask, TextFeature.ALIGNMENT_TYPE) ||
            TextStyleTag.isFeatureSupported(featureMask, TextFeature.NON_LENGTH_VERTICAL_ALIGN)
        ) {
            val alignmentType = contentData[mBufferOffset++]
            val verticalAlignCode = contentData[mBufferOffset++]

            if (TextStyleTag.isFeatureSupported(featureMask, TextFeature.ALIGNMENT_TYPE)) {
                styleTag.setAlignmentType(alignmentType)
            }
            if (TextStyleTag.isFeatureSupported(
                    featureMask,
                    TextFeature.NON_LENGTH_VERTICAL_ALIGN
                )
            ) {
                styleTag.setVerticalAlignCode(verticalAlignCode)
            }
        }

        // TODO:字体相关暂时不处理

        if (TextStyleTag.isFeatureSupported(featureMask, TextFeature.FONT_FAMILY)) {
            /*styleTag.setFontFamilies(myFontManager, data.get(dataOffset++) as Short)*/
            mBufferOffset += 2

        }
        if (TextStyleTag.isFeatureSupported(featureMask, TextFeature.FONT_STYLE_MODIFIER)) {
            mBufferOffset += 2
/*            val value = data.get(dataOffset++) as Short
            styleTag.setFontModifiers(
                (value and 0xFF) as Byte,
                (value shr 8 and 0xFF) as Byte
            )*/
        }
        return styleTag
    }

    private fun readStyleCloseTag(): TextTag {
        return TextTag.StyleCloseTag
    }

    private fun readFixedHSpaceTag(): TextTag {
        val fixedHSpaceLength = contentData[mBufferOffset]
        mBufferOffset += 2
        return TextFixedHSpaceTag(fixedHSpaceLength.toInt())
    }

    private fun readImageTag(): TextTag {
        // 读取 image 所属的 id
        val idArr = contentData.copyOfRange(mBufferOffset, mBufferOffset + 2)
        val id = TextByteToBasicUtil.toUInt16(idArr)
        mBufferOffset += 2
        // 根据 id 获取 image
        val textImage = textResource.getImage(id)

        // TODO:假设一定能够获取到的数据
        return TextImageTag(textImage!!)
    }

    private fun readHyperlinkControlTag(): TextTag {
        return TextHyperlinkControlTag()
    }
}

