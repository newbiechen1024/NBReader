package com.example.newbiechen.nbreader.ui.component.book.text.processor

import android.content.Context
import android.graphics.PorterDuff
import android.text.TextUtils
import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.ui.component.book.plugin.NativeFormatPlugin
import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextElementArea
import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextLineInfo
import com.example.newbiechen.nbreader.ui.component.book.text.entity.element.TextElement
import com.example.newbiechen.nbreader.ui.component.book.text.entity.element.TextWordElement
import com.example.newbiechen.nbreader.ui.component.book.text.entity.textstyle.TextAlignmentType
import com.example.newbiechen.nbreader.ui.component.book.text.hyphenation.TextHyphenInfo
import com.example.newbiechen.nbreader.ui.component.book.text.hyphenation.TextTeXHyphenator
import com.example.newbiechen.nbreader.ui.component.book.text.processor.cursor.TextParagraphCursor
import com.example.newbiechen.nbreader.ui.component.book.text.processor.cursor.TextWordCursor
import com.example.newbiechen.nbreader.ui.component.widget.page.PageTextView
import com.example.newbiechen.nbreader.ui.component.widget.page.PageType
import com.example.newbiechen.nbreader.ui.component.widget.page.PageView
import com.example.newbiechen.nbreader.uilts.LogHelper
import java.util.HashMap

/**
 *  author : newbiechen
 *  date : 2019-10-20 18:50
 *  description :文本处理器，处理与绘制页面
 */

class TextProcessor(private val context: Context) : BaseTextProcessor(context) {

    // 上一页文本
    private var mPrevPage = TextPage()
    // 当前页文本
    private var mCurPage = TextPage()
    // 下一页文本
    private var mNextPage = TextPage()

    // 文本模块
    private var mTextModel: TextModel? = null

    private object SizeUnit {
        const val PIXEL_UNIT = 0
        const val LINE_UNIT = 1
    }

    private data class ParagraphSize(
        var height: Int = 0,
        var topMargin: Int = 0,
        var bottomMargin: Int = 0
    )

    companion object {
        private val SPACE = charArrayOf(' ')
        private const val TAG = "TextProcessor"
    }

    /**
     * 设置文本资源
     */
    @Synchronized
    fun setTextResource(plugin: NativeFormatPlugin) {
        mTextModel = TextModel(plugin)

        mTextModel!!.getChapterCursor(0)

        // 重置成员变量
        mPrevPage.reset()
        mCurPage.reset()
        mNextPage.reset()

        // 如果 model 中存在段落
        if (mTextModel!!.getChapterCount() > 0) {
            // TODO: 章节中一定存在段落，这个是否需要保证？
            // 初始化当前页面光标，传入第一章的第一段
            mCurPage.initCursor(mTextModel!!.getChapterCursor(0).getParagraphCursor(0))
        }

        // 通知页面无效
        pageInvalidate()
    }

    /**
     * 通知页面切换
     */
    @Synchronized
    fun turnPage(pageType: PageType) {
        when (pageType) {
            // 翻阅到上一页
            PageType.PREVIOUS -> {
                val swap = mNextPage
                // 当前页作为下一页
                mNextPage = mCurPage
                // 上一页作为当前页
                mCurPage = mPrevPage
                // 下一页被作为上一页的缓冲
                mPrevPage = swap
                // 重置上一页
                mPrevPage.reset()

                // 判断当前页的状态
                when (mCurPage.pageState) {
                    TextPage.State.NONE -> {
                        // 先准备下一页
                        preparePage(mNextPage)
                        // 下一页信息准备失败
                        if (mNextPage.isStartCursorPrepared()) {
                            // 下一页的起始指针，作为当前页的结尾指针
                            mCurPage.initCursor(mNextPage.startWordCursor!!, false)
                        }
                    }
                    TextPage.State.PREPARED -> {
                        mNextPage.reset()
                        // 当前页的结尾指针，作为下一页的起始指针
                        mNextPage.initCursor(mCurPage.endWordCursor!!, true)
                    }
                }
            }
            PageType.NEXT -> {
                val swap = mPrevPage
                mPrevPage = mCurPage
                mCurPage = mNextPage
                mNextPage = swap
                mNextPage.reset()

                when (mCurPage.pageState) {
                    TextPage.State.NONE -> {
                        // 准备上一页的数据
                        preparePage(mPrevPage)
                        // 配置当前页
                        if (mPrevPage.isEndCursorPrepared()) {
                            mCurPage.initCursor(mPrevPage.endWordCursor!!, true)
                        }
                    }
                    TextPage.State.PREPARED -> {
                        // 配置下一页
                        mNextPage.initCursor(mCurPage.endWordCursor!!, true)
                    }
                }
            }
        }
    }

    /**
     * 是否页面存在
     */
    @Synchronized
    fun hasPage(type: PageType): Boolean {
        when (type) {
            PageType.PREVIOUS -> {
                val startWordCursor = getCurPageStartCursor()
                return !(startWordCursor?.isStartOfText() ?: true)
            }
            PageType.NEXT -> {
                val endWordCursor = getCurPageEndCursor()
                return !(endWordCursor?.isEndOfText() ?: true)
            }
        }
        return false
    }

    /**
     * 获取当前页面的起始光标
     */
    @Synchronized
    fun getCurPageStartCursor(): TextWordCursor? {
        if (!mCurPage.isStartCursorPrepared()) {
            preparePage(mCurPage)
        }
        return mCurPage.startWordCursor
    }

    // 获取当前页面的终止光标
    @Synchronized
    fun getCurPageEndCursor(): TextWordCursor? {
        if (!mCurPage.isEndCursorPrepared()) {
            preparePage(mCurPage)
        }
        return mCurPage.endWordCursor
    }

    /**
     * 绘制页面，drawPage 只能绘制当前页的前一页和后一页。所以继续绘制下一页需要先进行
     * @see turnPage 翻页操作
     * @param canvas:被绘制的画布
     * @param pageType:绘制的页面类型
     */
    @Synchronized
    override fun drawInternal(canvas: TextCanvas, pageType: PageType) {
        // 如果 textModel 不存在直接 return
        if (mTextModel == null || mTextModel!!.getChapterCount() == 0) {
            return
        }

        LogHelper.i(TAG, "drawInternal: ")

        // 获取并初始化待处理 Page
        val page: TextPage = when (pageType) {
            PageType.PREVIOUS -> {
                // 处理上一页之前，需要准备当前页
                if (mPrevPage.pageState == TextPage.State.NONE) {
                    // 先准备当前页面
                    preparePage(mCurPage)

                    // 初始化页面的光标位置
                    if (mCurPage.isStartCursorPrepared()) {
                        // 将 curPage 的起始光标，设置为 PrePage 的末尾光标
                        mPrevPage.initCursor(mCurPage.startWordCursor!!, false)
                    }
                }
                mPrevPage
            }

            PageType.CURRENT -> {
                mCurPage
            }

            PageType.NEXT -> {
                // 如果下一页没有缓存
                if (mNextPage.pageState == TextPage.State.NONE) {
                    // 先准备当前页面
                    preparePage(mCurPage)
                    if (mCurPage.isEndCursorPrepared()) {
                        // 将 curPage 末尾光标，设置为 NextPage 起始光标
                        mNextPage.initCursor(mCurPage.endWordCursor!!, true)
                    }
                }
                mNextPage
            }
        }

        // 清空元素绘制区域数据
        page.textElementAreaVector.clear()

        // 准备待处理的页
        preparePage(page)

        LogHelper.i(TAG, "preparePage: ")

        // 如果结果不为 prepare 直接 return
        if (page.pageState != TextPage.State.PREPARED) {
            return
        }
        LogHelper.i(TAG, "preparePage success")


        // 准本文本绘制区域，并返回每个段落对应 textArea 的起始位置
        val labels = prepareTextArea(page)

        LogHelper.i(TAG, "prepareTextArea")

        // 绘制页面
        drawPage(canvas, page, labels)

        LogHelper.i(TAG, "drawPage")

        // 绘制高亮区域

        // 绘制下划线区域

        // 绘制之前已选中区域
    }

    @Synchronized
    fun preparePage(pageType: PageType) {
        val page = when (pageType) {
            PageType.PREVIOUS -> mPrevPage
            PageType.CURRENT -> mCurPage
            PageType.NEXT -> mNextPage
        }

        preparePage(page)
    }

    /**
     * 准备处理页面数据
     */
    private fun preparePage(page: TextPage) {
        // 设置页面的视口
        page.setViewPort(viewWidth, viewHeight)

        // 如果未准备任何信息，或者已经准备完成，都直接 return
        if (page.pageState == TextPage.State.NONE || page.pageState == TextPage.State.PREPARED) {
            return
        }

        // 获取 Page 的旧状态
        val oldPageState = page.pageState
        // 将 Page 中的 lineInfo 加入到缓存中。
        for (lineInfo in page.lineInfoList) {
            mLineInfoCache[lineInfo] = lineInfo
        }

        // 根据当前 page 状态做相应绘制操作
        when (page.pageState) {
            // 已知起始光标状态
            TextPage.State.KNOW_START_CURSOR -> {
                if (page.isStartCursorPrepared()) {
                    preparePageInternal(page, page.startWordCursor!!, page.endWordCursor!!)
                }
            }
            // 已知结束光标状态
            TextPage.State.KNOW_END_CURSOR -> {
                if (page.isEndCursorPrepared()) {
                    // 根据结尾光标查找页面的起始光标
                    val startWordCursor = findPageStartCursor(page)
                    // 如果查找到了起始光标
                    if (startWordCursor != null) {
                        page.startWordCursor!!.updateCursor(startWordCursor)
                        preparePageInternal(page, startWordCursor, page.endWordCursor!!)
                    }
                }
            }
        }

        // 更新当前 Page 状态
        page.setPageState(TextPage.State.PREPARED)

        // 清除行缓存信息
        mLineInfoCache.clear()

        // 如果页面是当前页
        if (page === mCurPage) {
            // 如果当前页已知结束位置，那么 PrePage 一定是无效的，直接重置
            if (oldPageState != TextPage.State.KNOW_START_CURSOR) {
                mPrevPage.reset()
            }

            // 如果当前页已知起始位置，那么 NextPage 一定是无效的，直接重置
            if (oldPageState != TextPage.State.KNOW_END_CURSOR) {
                mNextPage.reset()
            }
        }
    }

    /**
     * 查找页面的起始光标
     * @param page：需要被查找的页面
     * @param
     */
    private fun findPageStartCursor(page: TextPage): TextWordCursor? {
        // 如果结尾未知 那么直接 return
        if (!page.isEndCursorPrepared()) {
            return null
        }

        // chapter 情况下 startCursor 有问题
        return findPageStartCursorInternal(
            page,
            page.endWordCursor!!,
            SizeUnit.PIXEL_UNIT,
            page.viewHeight
        )
    }

    private fun findPageStartCursorInternal(
        page: TextPage,
        end: TextWordCursor,
        unit: Int,
        height: Int
    ): TextWordCursor {

        // TODO：段落向前翻页的处理。(当前逻辑有问题)

        var remainHeight = height
        val startCursor = TextWordCursor(end)

        // 获取最后一段的高度
        var size = paragraphSize(page, startCursor, true, unit)
        // 当前高度减去最后一段
        remainHeight -= size.height
        // 判断当前光标是否指向段落的起始位置
        var positionChanged = !startCursor.isStartOfParagraph()
        // 指向段落的起始位置(因为最后一段的数据，已经处理了)
        startCursor.moveToParagraphStart()

        // 光标从下向上移动
        while (remainHeight > 0) {
            // 上一段的高度
            val previousSize = size
            if (positionChanged && startCursor.getParagraphCursor().isEndOfSection()) {
                break
            }
            // 跳转到上一个段落
            if (!startCursor.moveToPrevParagraph()) {
                break
            }

            if (!startCursor.getParagraphCursor().isEndOfSection()) {
                positionChanged = true
            }
            // 获取当前段落的高度
            size = paragraphSize(page, startCursor, false, unit)
            //
            remainHeight -= size.height
            // 如果上一段落不为空，则需要加上底的高度
            if (previousSize != null) {
                remainHeight += size.bottomMargin.coerceAtMost(previousSize.topMargin)
            }
        }

        // 如果段落的高度，超出区域时需要根据超出的 height，进行缩小处理
        skip(page, startCursor, unit, -remainHeight)

        if (unit == SizeUnit.PIXEL_UNIT) {
            // 如果起始光标指向的位置等于末尾光标指向的位置
            var sameStart = startCursor.isSamePosition(end)
            // 检测由于光标位置不同，导致 samePosition 判断失败的问题。
            if (!sameStart && startCursor.isEndOfParagraph() && end.isStartOfParagraph()) {
                val startCopy = TextWordCursor(startCursor)
                startCopy.moveToNextParagraph()
                sameStart = startCopy.isSamePosition(end)
            }
            // 如果指向的是同一个位置，则再进行匹配
            if (sameStart) {
                startCursor.updateCursor(
                    findPageStartCursorInternal(
                        page,
                        end,
                        SizeUnit.LINE_UNIT,
                        1
                    )
                )
            }
        }

        return startCursor
    }

    /**
     *
     */
    private fun paragraphSize(
        page: TextPage,
        cursor: TextWordCursor,
        beforeCurrentPosition: Boolean,
        unit: Int
    ): ParagraphSize {
        val size = ParagraphSize()
        val paragraphCursor = cursor.getParagraphCursor()
        val endElementIndex =
            if (beforeCurrentPosition) cursor.getElementIndex() else paragraphCursor.getElementCount()

        resetTextStyle()

        var elementIndex = 0
        var charIndex = 0
        var info: TextLineInfo? = null
        while (elementIndex < endElementIndex) {
            val prev = info
            info =
                prepareTextLine(
                    page,
                    paragraphCursor,
                    elementIndex,
                    charIndex,
                    endElementIndex,
                    prev
                )
            elementIndex = info!!.endElementIndex
            charIndex = info!!.endCharIndex
            size.height += getTextLineHeight(info, unit)
            if (prev == null) {
                size.topMargin = info!!.vSpaceBefore
            }
            size.bottomMargin = info!!.vSpaceAfter
        }
        return size
    }

    private fun getTextLineHeight(info: TextLineInfo, unit: Int): Int {
        return if (unit == SizeUnit.PIXEL_UNIT) info.height + info.descent + info.vSpaceAfter else if (info.isVisible) 1 else 0
    }

    private fun skip(page: TextPage, cursor: TextWordCursor, unit: Int, size: Int) {
        var size = size
        val paragraphCursor = cursor.getParagraphCursor()
        val endElementIndex = paragraphCursor.getElementCount()

        // 向上查找之前使用的元素样式，并应用
        resetTextStyle()
        applyStyleChange(paragraphCursor, 0, cursor.getElementIndex())

        var info: TextLineInfo? = null
        while (!cursor.isEndOfParagraph() && size > 0) {
            // 计算行大小
            info = prepareTextLine(
                page,
                paragraphCursor,
                cursor.getElementIndex(),
                cursor.getCharIndex(),
                endElementIndex,
                info
            )
            // 跳转到行位置
            cursor.moveTo(info!!.endElementIndex, info!!.endCharIndex)
            size -= getTextLineHeight(info, unit)
        }
    }


    /**
     * 根据光标，填充 page 信息
     */
    private fun preparePageInternal(
        page: TextPage,
        startWordCursor: TextWordCursor,
        endWordCursor: TextWordCursor
    ) {
        // 将结尾光标设置为起始光标，用于进行遍历查找
        val findWordCursor = endWordCursor

        findWordCursor.updateCursor(startWordCursor)
        page.lineInfoList.clear()

        var curLineInfo: TextLineInfo? = null
        // 是否存在下一段
        var hasNextParagraph = false
        // 剩余可用高度
        var remainAreaHeight = page.viewHeight

        do {
            // 重置文本样式
            resetTextStyle()
            // 上一段行信息
            val preLineInfo = curLineInfo

            // 获取当前光标指向的位置
            val paragraphCursor = findWordCursor.getParagraphCursor()
            val curElementIndex = findWordCursor.getElementIndex()
            val curCharIndex = findWordCursor.getCharIndex()
            // 获取最后一个元素的索引
            val endElementIndex = paragraphCursor.getElementCount()

            // 创建新的行信息
            curLineInfo =
                TextLineInfo(paragraphCursor, curElementIndex, curCharIndex, getTextStyle())

            // 循环遍历 element
            while (curLineInfo!!.endElementIndex < endElementIndex) {

                // 填充 textLine
                curLineInfo = prepareTextLine(
                    page,
                    paragraphCursor,
                    curLineInfo!!.endElementIndex,
                    curLineInfo!!.endCharIndex,
                    endElementIndex, preLineInfo
                )

                // 剩余高度 = 当前行高度 - 行高
                remainAreaHeight -= (curLineInfo.height + curLineInfo.descent)

                // 如果剩余高度不足，则 break
                if (remainAreaHeight <= 0) {
                    break
                }

                remainAreaHeight -= curLineInfo.vSpaceAfter

                // 光标指向新行的末尾
                findWordCursor.moveTo(curLineInfo.endElementIndex, curLineInfo.endCharIndex)
                // 将 line 添加到 page 中
                page.lineInfoList.add(curLineInfo)
            }

            // TODO:这里需要检测如果是章节的最后一个段落，nextParagraph 为 false。同时支持将光标移动到 nextParagraph

            // 是否存在下一段落
            hasNextParagraph =
                findWordCursor.isEndOfParagraph() && findWordCursor.moveToNextParagraph()

            // 存在下一段且不为片段结束段落、存在剩余空间
        } while (hasNextParagraph && remainAreaHeight > 0 && !paragraphCursor.isEndOfSection())

        // 重置文本样式
        resetTextStyle()
    }


    // 缓存文本行信息
    private val mLineInfoCache = HashMap<TextLineInfo, TextLineInfo>()

    private fun prepareTextLine(
        page: TextPage,
        paragraphCursor: TextParagraphCursor,
        startElementIndex: Int,
        startCharIndex: Int,
        endElementIndex: Int,
        preLineInfo: TextLineInfo?
    ): TextLineInfo {
        // 创建一个 TextLine
        val curLineInfo =
            TextLineInfo(paragraphCursor, startElementIndex, startCharIndex, getTextStyle())

        // 查看是否存在缓存好的 lineInfo 数据
        val cachedInfo = mLineInfoCache[curLineInfo]

        // 如果存在
        if (cachedInfo != null) {
            // 验证上一行内容
            cachedInfo.adjust(preLineInfo)
            // 应用指向的 element 的 style
            applyStyleChange(paragraphCursor, startElementIndex, cachedInfo.endElementIndex)
            return cachedInfo
        }

        // 索引标记
        var curElementIndex = startElementIndex
        var curCharIndex = startCharIndex

        // 是否是段落中的第一行
        var isFirstLine = startElementIndex == 0 && startCharIndex == 0

        if (isFirstLine) {
            // 获取当前元素
            var element = paragraphCursor.getElement(curElementIndex)!!

            if (element != null) {
                // 判断是否是文本样式元素
                while (isStyleElement(element!!)) {
                    // 使用该元素
                    applyStyleElement(element!!)
                    // 行起始位置向后移动一位
                    ++curElementIndex
                    curCharIndex = 0

                    // 如果起始位置指向到末尾位置
                    if (curElementIndex >= endElementIndex) {
                        break
                    }

                    // 获取下一个元素
                    element = paragraphCursor.getElement(curElementIndex)!!
                }
            }

            // 设置当前行的 Style
            curLineInfo.startStyle = getTextStyle()
            // 设置不具有改变 Style Element 的起始索引位置
            curLineInfo.realStartElementIndex = curElementIndex
            curLineInfo.realStartCharIndex = curCharIndex
        }

        // 获取当前样式
        var curTextStyle = getTextStyle()
        // 获取可绘制宽度 = 页面的宽度 - 右缩进
        val maxWidth = page.viewWidth - curTextStyle.getRightIndent(getMetrics())
        // 获取默认的缩进距离s
        curLineInfo.leftIndent = curTextStyle.getLeftIndent(getMetrics())

        // 如果是第一行，且不为居中显示，则计算第一行的左缩进
        if (isFirstLine && curTextStyle.getAlignment() !== TextAlignmentType.ALIGN_CENTER) {
            curLineInfo.leftIndent += curTextStyle.getFirstLineIndent(getMetrics())
        }

        // 如果 LeftIndent 太大了，则缩小
        if (curLineInfo.leftIndent > maxWidth - 20) {
            curLineInfo.leftIndent = maxWidth * 3 / 4
        }

        // 当前行的宽度，暂时为左缩进的宽度
        curLineInfo.width = curLineInfo.leftIndent

        // 如果实际起始位置为终止位置
        if (curLineInfo.realStartCharIndex == endElementIndex) {
            // 重置 end 信息
            curLineInfo.endElementIndex = curLineInfo.realStartElementIndex
            curLineInfo.endCharIndex = curLineInfo.realStartCharIndex
            return curLineInfo
        }

        //
        var newWidth = curLineInfo.width
        var newHeight = curLineInfo.height
        var newDescent = curLineInfo.descent

        // 是否碰到了单词
        var wordOccurred = false
        var isVisible = false
        var lastSpaceWidth = 0
        // 空格数统计
        var internalSpaceCount = 0
        var removeLastSpace = false


        // 遍历 element 填充 TextLine
        while (curElementIndex < endElementIndex) {
            // 获取当前元素
            var element = paragraphCursor.getElement(curElementIndex)!!
            // 获取 Element 的宽度
            newWidth += getElementWidth(element, curCharIndex)
            // 获取 Element 最大高度
            newHeight = newHeight.coerceAtLeast(getElementHeight(element))
            // 文字距离基准线的最大距离
            newDescent = newDescent.coerceAtLeast(getElementDescent(element))

            // 根据 element 类型决定相应的处理方式
            if (element === TextElement.HSpace) {
                // 如果碰到了单词
                if (wordOccurred) {
                    wordOccurred = false
                    internalSpaceCount++
                    lastSpaceWidth = mPaintContext.getSpaceWidth()
                    newWidth += lastSpaceWidth
                }
            } else if (element === TextElement.NBSpace) {
                wordOccurred = true
            } else if (element is TextWordElement) {
                wordOccurred = true
                isVisible = true
            } else if (isStyleElement(element)) {
                applyStyleElement(element)
            }

            // 如果是元素偏移或者是文字元素造成当前测量宽度大于最大宽度的情况
            if (newWidth > maxWidth &&
                (curLineInfo.endElementIndex != startElementIndex || element is TextWordElement)
            ) {
                break
            }


            // 获取下一个元素。(用于解决换行的问题)

            ++curElementIndex
            curCharIndex = 0

            val previousElement = element

            // 判断是否到达了元素的结尾后一位 (处理完最后一个元素，判断换行)
            var allowBreak = curElementIndex >= endElementIndex

            // 不允许换行的情况
            if (!allowBreak) {
                // 新的 element
                element = paragraphCursor.getElement(curElementIndex)!!

                // 如果存在下列情况，进行强制换行
                allowBreak = previousElement !== TextElement.NBSpace &&
                        element !== TextElement.NBSpace &&
                        (element !is TextWordElement || previousElement is TextWordElement)
            }

            // 允许换行的情况，将计算的结果赋值给 TextLineInfo
            if (allowBreak) {
                curLineInfo.isVisible = isVisible
                curLineInfo.width = newWidth

                if (curLineInfo.height < newHeight) {
                    curLineInfo.height = newHeight
                }

                if (curLineInfo.descent < newDescent) {
                    curLineInfo.descent = newDescent
                }

                curLineInfo.endElementIndex = curElementIndex
                curLineInfo.endCharIndex = curCharIndex
                curLineInfo.spaceCount = internalSpaceCount

                curTextStyle = getTextStyle()
                removeLastSpace = !wordOccurred && internalSpaceCount > 0
            }
        }

        // 如果当前元素位置没有到达段落的末尾，并且允许断字
        if (curElementIndex < endElementIndex &&
            (isHyphenationPossible() || curLineInfo.endElementIndex == startElementIndex)
        ) {
            // 获取当前元素
            val element = paragraphCursor.getElement(curElementIndex)
            if (element is TextWordElement) {
                // 宽需要减去当前 element 的宽
                newWidth -= getWordWidth(element, curCharIndex)
                // 获取最大宽度和当前宽的差值，等于空格的宽度
                var remainSpaceWidth = maxWidth - newWidth
                // 如果当前元素大于 3 字节，并且剩余控件大于 2 倍的空格
                // 或者单个元素独占一行
                if ((element.length > 3 && remainSpaceWidth > 2 * mPaintContext.getSpaceWidth())
                    || curLineInfo.endElementIndex == startElementIndex
                ) {
                    // 获取断句信息
                    val hyphenInfo = getHyphenationInfo(element)

                    var hyphenIndex = curCharIndex
                    var subWordWidth = 0

                    var wordCurCharIndex = curCharIndex
                    var wordEndCharIndex = element.length - 1

                    // 如果末尾字节大于当前字节
                    while (wordEndCharIndex > wordCurCharIndex) {
                        // 取 word 的中间单词
                        val mid = (wordEndCharIndex + wordCurCharIndex + 1) / 2
                        var tempMid = mid
                        // 从 wordCurCharIndex 到 tempMid 之间查找支持 Hyphenation 的单词
                        while (tempMid > wordCurCharIndex && !hyphenInfo.isHyphenationPossible(
                                tempMid
                            )
                        ) {
                            --tempMid
                        }

                        // 如果查找到了
                        if (tempMid > wordCurCharIndex) {
                            // 获取单词的宽
                            val wordWidth = getWordWidth(
                                element,
                                curCharIndex,
                                tempMid - curCharIndex,
                                element.data[element.offset + tempMid - 1] !== '-'
                            )
                            // 如果单词的宽小于剩余空间
                            if (wordWidth < remainSpaceWidth) {
                                wordCurCharIndex = mid
                                hyphenIndex = tempMid
                                subWordWidth = wordWidth
                            } else {
                                wordEndCharIndex = mid - 1
                            }
                        } else {
                            wordCurCharIndex = mid
                        }
                    }

                    if (hyphenIndex == curCharIndex && curLineInfo.endElementIndex == startElementIndex) {
                        subWordWidth = getWordWidth(element, curCharIndex, 1, false)
                        var right =
                            if (element.length === curCharIndex + 1) element.length else element.length - 1
                        var left = curCharIndex + 1
                        while (right > left) {
                            val mid = (right + left + 1) / 2
                            val w = getWordWidth(
                                element,
                                curCharIndex,
                                mid - curCharIndex,
                                element.data[element.offset + mid - 1] !== '-'
                            )
                            if (w <= remainSpaceWidth) {
                                left = mid
                                subWordWidth = w
                            } else {
                                right = mid - 1
                            }
                        }
                        hyphenIndex = right
                    }

                    // 重置 TextLineo 的样式
                    if (hyphenIndex > curCharIndex) {
                        curLineInfo.isVisible = true
                        curLineInfo.width = newWidth + subWordWidth
                        if (curLineInfo.height < newHeight) {
                            curLineInfo.height = newHeight
                        }
                        if (curLineInfo.descent < newDescent) {
                            curLineInfo.descent = newDescent
                        }
                        curLineInfo.endElementIndex = curElementIndex
                        curLineInfo.endCharIndex = hyphenIndex
                        curLineInfo.spaceCount = internalSpaceCount

                        curTextStyle = getTextStyle()
                        removeLastSpace = false
                    }
                }
            }
        }

        // 是否移除最后一个空格
        if (removeLastSpace) {
            curLineInfo.width -= lastSpaceWidth
            curLineInfo.spaceCount--
        }

        setTextStyle(curTextStyle)

        // 是否是第一行
        if (isFirstLine) {
            curLineInfo.vSpaceBefore = curLineInfo.startStyle!!.getSpaceBefore(getMetrics())
            if (preLineInfo != null) {
                curLineInfo.previousInfoUsed = true
                curLineInfo.height += 0.coerceAtLeast(curLineInfo.vSpaceBefore - preLineInfo.vSpaceAfter)
            } else {
                curLineInfo.previousInfoUsed = false
                curLineInfo.height += curLineInfo.vSpaceBefore
            }
        }

        // 如果是段落的最后一行
        if (curLineInfo.isEndOfParagraph()) {
            curLineInfo.vSpaceAfter = getTextStyle().getSpaceAfter(getMetrics())
        }

        // 加入到缓存中
        if (curLineInfo.endElementIndex != endElementIndex || endElementIndex == curLineInfo.elementCount) {
            mLineInfoCache[curLineInfo] = curLineInfo
        }

        // 如果遍历有问题，那直接到末尾
        if (curLineInfo.endElementIndex == startCharIndex && curLineInfo.endElementIndex == startCharIndex) {
            curLineInfo.endElementIndex = paragraphCursor.getElementCount()
            curLineInfo.endCharIndex = 0
        }
        return curLineInfo
    }

    /**
     * 是否支持断句
     */
    private fun isHyphenationPossible(): Boolean {
        return getTextStyle().allowHyphenations()
    }

    private var mCachedWord: TextWordElement? = null
    private var mCachedInfo: TextHyphenInfo? = null

    /**
     * 获取断句信息
     */
    private fun getHyphenationInfo(word: TextWordElement): TextHyphenInfo {
        // 如果缓存的 word 无效
        if (mCachedWord !== word) {
            mCachedWord = word
            mCachedInfo = TextTeXHyphenator.getInstance().getHyphenInfo(word)
        }
        return mCachedInfo!!
    }

    /**
     * 根据 TextLine 设置文本绘制区域
     * @return 返回每行对应的 textElementAreaVector 的起始位置
     */
    private fun prepareTextArea(page: TextPage): IntArray {
        var x = 0
        var y = 0
        var previousInfo: TextLineInfo? = null
        // 记录每个位置对对应的 TextArea
        val labels = IntArray(page.lineInfoList.size + 1)
        // 遍历行
        for ((index, lineInfo) in page.lineInfoList.withIndex()) {
            lineInfo.adjust(previousInfo)
            // 根据 curLineInfo 信息准备绘制区域
            prepareTextAreaInternal(page, lineInfo, x, y)
            // 记录当前高度
            y += lineInfo.height + lineInfo.descent + lineInfo.vSpaceAfter
            // 标记下一个文本行，对应的 area 列表的起始索引
            labels[index + 1] = page.textElementAreaVector.size()
            // 设置当前行变为上一行
            previousInfo = lineInfo
        }
        return labels
    }

    /**
     * 根据文本行准备文本绘制区域信息
     */
    private fun prepareTextAreaInternal(
        page: TextPage,
        lineInfo: TextLineInfo,
        x: Int,
        y: Int
    ) {
        var x = x
        var y = y
        // 根据视口的最大高度取最小值
        y = (y + lineInfo.height).coerceAtMost(mTextConfig.topMargin + page.viewHeight - 1)

        val context = mPaintContext
        val paragraphCursor = lineInfo.paragraphCursor
        // 设置当前行的样式
        setTextStyle(lineInfo.startStyle!!)
        var spaceCount = lineInfo.spaceCount
        var fullCorrection = 0
        val isEndOfParagraph = lineInfo.isEndOfParagraph()
        // 是否碰到 word
        var isWordOccurred = false
        // 是否样式改变
        var isStyleChange = true

        x += lineInfo.leftIndent

        val maxWidth = page.viewWidth

        when (getTextStyle().getAlignment()) {
            TextAlignmentType.ALIGN_RIGHT -> x += maxWidth - getTextStyle().getRightIndent(
                getMetrics()
            ) - lineInfo.width
            TextAlignmentType.ALIGN_CENTER -> x += (maxWidth - getTextStyle().getRightIndent(
                getMetrics()
            ) - lineInfo.width) / 2
            TextAlignmentType.ALIGN_JUSTIFY -> if (!isEndOfParagraph && paragraphCursor.getElement(
                    lineInfo.endElementIndex
                ) !== TextElement.AfterParagraph
            ) {
                fullCorrection =
                    maxWidth - getTextStyle().getRightIndent(getMetrics()) - lineInfo.width
            }
            TextAlignmentType.ALIGN_LEFT, TextAlignmentType.ALIGN_UNDEFINED -> {
            }
        }

        val paragraph = lineInfo.paragraphCursor
        val chapterIndex = paragraph.getChapterIndex()
        val paragraphIndex = paragraph.getParagraphIndex()
        val endElementIndex = lineInfo.endElementIndex
        var charIndex = lineInfo.realStartCharIndex
        var spaceElement: TextElementArea? = null
        run {
            var wordIndex = lineInfo.realStartElementIndex
            while (wordIndex < endElementIndex) {
                // 获取 Element
                val element = paragraph.getElement(wordIndex)
                val width = getElementWidth(element!!, charIndex)
                // 如果是空格元素
                if (element === TextElement.HSpace) {
                    if (isWordOccurred && spaceCount > 0) {
                        val correction = fullCorrection / spaceCount
                        val spaceLength = context.getSpaceWidth() + correction
                        // 是否是下划线
                        spaceElement = if (getTextStyle().isUnderline()) {
                            TextElementArea(
                                chapterIndex,
                                paragraphIndex,
                                wordIndex,
                                0,
                                0, // length
                                isLastElement = true, // is last in element
                                addHyphenationSign = false, // add hyphenation sign
                                isStyleChange = false, // changed style
                                style = getTextStyle(),
                                element = element,
                                startX = x,
                                startY = x + spaceLength,
                                endX = y,
                                endY = y
                            )
                        } else {
                            null
                        }
                        x += spaceLength
                        fullCorrection -= correction
                        isWordOccurred = false
                        --spaceCount
                    }
                } else if (element is TextWordElement) {
                    val height = getElementHeight(element)
                    val descent = getElementDescent(element)
                    val length = if (element is TextWordElement) element.length else 0
                    if (spaceElement != null) {
                        page.textElementAreaVector.add(spaceElement!!)
                        spaceElement = null
                    }
                    page.textElementAreaVector.add(
                        TextElementArea(
                            chapterIndex,
                            paragraphIndex,
                            wordIndex,
                            charIndex,
                            length - charIndex,
                            isLastElement = true, // is last in element
                            addHyphenationSign = false, // add hyphenation sign
                            isStyleChange = isStyleChange,
                            style = getTextStyle(),
                            element = element,
                            startX = x,
                            startY = x + width - 1,
                            endX = y - height + 1,
                            endY = y + descent
                        )
                    )
                    isStyleChange = false
                    isWordOccurred = true
                } else if (isStyleElement(element)) {
                    applyStyleElement(element)
                    isStyleChange = true
                }
                x += width
                ++wordIndex
                charIndex = 0
            }
        }

        if (!isEndOfParagraph) {
            val len = lineInfo.endCharIndex
            if (len > 0) {
                val wordIndex = lineInfo.endElementIndex
                val wordElement = paragraph.getElement(wordIndex) as TextWordElement
                val addHyphenationSign = wordElement.data[wordElement.offset + len - 1] !== '-'
                val width = getWordWidth(wordElement, 0, len, addHyphenationSign)
                val height = getElementHeight(wordElement)
                val descent = context.getDescent()
                page.textElementAreaVector.add(
                    TextElementArea(
                        chapterIndex,
                        paragraphIndex,
                        wordIndex, 0, len,
                        false, // is last in element
                        addHyphenationSign,
                        isStyleChange,
                        getTextStyle(),
                        wordElement,
                        x, x + width - 1, y - height + 1, y + descent
                    )
                )
            }
        }
    }

    /**
     * 绘制页面
     * @param lineOfAreaIndexArr:每个 textLine 对应 TextElementAreaVector 的起始位置s
     */
    private fun drawPage(canvas: TextCanvas, page: TextPage, lineOfAreaIndexArr: IntArray) {
        // 循环遍历文本行
        page.lineInfoList.forEachIndexed { index, textLineInfo ->
            drawTextLine(
                canvas,
                page,
                textLineInfo,
                lineOfAreaIndexArr[index],
                lineOfAreaIndexArr[index + 1]
            )
        }
    }

    /**
     * 绘制文本行
     */
    private fun drawTextLine(
        canvas: TextCanvas,
        page: TextPage,
        lineInfo: TextLineInfo,
        fromAreaIndex: Int,
        toAreaIndex: Int
    ) {
        val paragraph = lineInfo.paragraphCursor
        var areaIndex = fromAreaIndex
        val endElementIndex = lineInfo.endElementIndex
        var charIndex = lineInfo.realStartCharIndex
        val pageAreas = page.textElementAreaVector.areas()

        if (toAreaIndex > pageAreas.size) {
            return
        }
        // 循环元素
        var wordIndex = lineInfo.realStartElementIndex
        while (wordIndex < endElementIndex && areaIndex < toAreaIndex) {
            val element = paragraph.getElement(wordIndex)
            val area = pageAreas[areaIndex]
            if (element === area.element) {
                ++areaIndex
                // 如果当前样式存在改变
                if (area.isStyleChange) {
                    setTextStyle(area.style)
                }

                val areaX = area.startX
                val areaY =
                    area.endY - getElementDescent(element) - getTextStyle().getVerticalAlign(
                        getMetrics()
                    )
                if (element is TextWordElement) {
                    drawWord(
                        canvas,
                        areaX,
                        areaY,
                        element,
                        charIndex,
                        -1,
                        false,
                        mTextConfig.textColor
                    )
                } else if (element === TextElement.HSpace || element === TextElement.NBSpace) {
                    val cw = mPaintContext.getSpaceWidth()
                    var len = 0
                    while (len < area.endX - area.startX) {
                        canvas.drawString(areaX + len, areaY, SPACE, 0, 1, mPaintContext)
                        len += cw
                    }
                }
            }
            ++wordIndex
            charIndex = 0
        }

        if (areaIndex < toAreaIndex) {
            val area = pageAreas[areaIndex++]
            if (area.isStyleChange) {
                setTextStyle(area.style)
            }
            val start = if (lineInfo.startElementIndex == lineInfo.endElementIndex) {
                lineInfo.startCharIndex
            } else 0

            val len = lineInfo.endCharIndex - start
            val word = paragraph.getElement(lineInfo.endElementIndex) as TextWordElement

            drawWord(
                canvas,
                area.startX,
                area.endY - mPaintContext.getDescent() - getTextStyle().getVerticalAlign(
                    getMetrics()
                ),
                word,
                start,
                len,
                area.addHyphenationSign, mTextConfig.textColor
            )
        }
    }
}