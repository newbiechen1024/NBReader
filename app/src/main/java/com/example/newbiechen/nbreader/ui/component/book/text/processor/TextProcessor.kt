package com.example.newbiechen.nbreader.ui.component.book.text.processor

import com.example.newbiechen.nbreader.ui.component.book.text.TextModel
import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextElementArea
import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextFixedPosition
import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextLineInfo
import com.example.newbiechen.nbreader.ui.component.book.text.entity.element.TextElement
import com.example.newbiechen.nbreader.ui.component.book.text.entity.element.TextWordElement
import com.example.newbiechen.nbreader.ui.component.book.text.entity.textstyle.TextAlignmentType
import com.example.newbiechen.nbreader.ui.component.book.text.hyphenation.TextHyphenInfo
import com.example.newbiechen.nbreader.ui.component.book.text.hyphenation.TextTeXHyphenator
import com.example.newbiechen.nbreader.ui.component.widget.page.PageType
import com.example.newbiechen.nbreader.ui.component.widget.page.PageView

/**
 *  author : newbiechen
 *  date : 2019-10-20 18:50
 *  description :文本处理器，处理与绘制页面
 */

class TextProcessor(private val pageView: PageView) : BaseTextProcessor() {
    // 上一页文本
    private var mPrePage = TextPage()
    // 当前页文本
    private var mCurPage = TextPage()
    // 下一页文本
    private var mNextPage = TextPage()
    // 文本模块
    private var mTextModel: TextModel? = null
    // 段落光标管理器
    private var mCursorManager: TextCursorManager? = null

    companion object {
        private val SPACE = charArrayOf(' ')
    }

    /**
     * 设置新的文本模块
     */
    fun setTextModel(textModel: TextModel) {
        mTextModel = textModel
        // 创建新的光标管理器
        mCursorManager = TextCursorManager(textModel)

        // 初始化成员变量
        mPrePage.clear()
        mCurPage.clear()
        mNextPage.clear()
    }

    /**
     * 通知页面切换。
     */
    fun turnPage(pageType: PageType) {
        when (pageType) {
            // 翻阅到上一页
            PageType.PREVIOUS -> {
                val swap = mNextPage
                // 当前页作为下一页
                mNextPage = mCurPage
                // 上一页作为当前页
                mCurPage = mPrePage
                // 下一页被作为上一页的缓冲液
                mPrePage = swap
                mPrePage.reset()

                // TODO:上一页的信息可能为 KNOW_XXX 的状态，所以下面的代码有问题

                // 根据当前页的状态
                when (mCurPage.pageState) {
                    TextPage.State.NONE -> {
                        // 准备下一页
                        preparePage(mNextPage)
                        // 下一页的起始指针，作为当前页的结尾指针
                        mCurPage.setPageCursor(mNextPage.startWordCursor!!, false)
                    }
                    TextPage.State.PREPARED -> {
                        mNextPage.reset()
                        // 当前页的结尾指针，作为下一页的起始指针
                        mNextPage.setPageCursor(mCurPage.endWordCursor!!, true)
                    }
                }
            }
            PageType.NEXT -> {
                val swap = mPrePage
                mPrePage = mCurPage
                mCurPage = mNextPage
                mNextPage = swap
                mNextPage.reset()

                when (mCurPage.pageState) {
                    TextPage.State.NONE -> {
                        preparePage(mPrePage)
                        mCurPage.setPageCursor(mPrePage.endWordCursor!!, true)
                    }
                    TextPage.State.PREPARED -> {
                        mNextPage.setPageCursor(mCurPage.endWordCursor!!, true)
                    }
                }
            }
        }

        // 切换页面

        // 是否需要判断 TextModel
    }

    /**
     * 是否页面存在
     */
    fun hasPage(type: PageType): Boolean {
        when (type) {
            PageType.PREVIOUS -> {
                val startWordCursor = getCurPageStartCursor()
                return startWordCursor.isStartOfText()
            }
            PageType.NEXT -> {
                val endWordCursor = getCurPageEndCursor()
                return endWordCursor.isEndOfText()
            }
        }
        return true
    }

    /**
     * 请求刷新
     */
    fun posInvalidate(){
        pageView.postInvalidate()
    }

    /**
     * 获取当前页面的起始光标
     */
    fun getCurPageStartCursor(): TextWordCursor {
        if (mCurPage.startWordCursor == null) {
            preparePage(mCurPage)
        }
        return mCurPage.startWordCursor!!
    }

    // TODO:这里好像有点问题，因为我赋值的时候连 startWordCursor == endWordCursor 这个流程需要之后考虑一下

    // 获取当前页面的终止光标
    fun getCurPageEndCursor(): TextWordCursor {
        if (mCurPage.endWordCursor == null) {
            preparePage(mCurPage)
        }
        return mCurPage.endWordCursor!!
    }


    /**
     * 绘制页面，drawPage 只能绘制当前页的前一页和后一页。所以继续绘制下一页需要先进行
     * @see turnPage 翻页操作
     * @param canvas:被绘制的画布
     * @param pageType:绘制的页面类型
     */
    override fun drawInternal(canvas: TextCanvas, pageType: PageType) {
        // 如果禁止绘制直接 return

        // 获取并初始化待处理 Page
        val page: TextPage = when (pageType) {
            PageType.PREVIOUS -> {
                // 处理上一页之前，需要准备当前页
                if (mPrePage.pageState == TextPage.State.NONE) {
                    // 先准备当前页面
                    preparePage(mCurPage)
                    // 初始化页面的光标位置
                    // 将 curPage 的起始光标，设置为 PrePage 的末尾光标
                    mPrePage.setPageCursor(mCurPage.startWordCursor!!, false)
                }
                mPrePage
            }

            PageType.CURRENT -> {
                mCurPage
            }

            PageType.NEXT -> {
                // 如果下一页没有缓存
                if (mNextPage.pageState == TextPage.State.NONE) {
                    // 先准备当前页面
                    preparePage(mCurPage)
                    // 将 curPage 末尾光标，设置为 NextPage 起始光标
                    mNextPage.setPageCursor(mCurPage.endWordCursor!!, true)
                }
                mNextPage
            }
        }

        // 清空元素绘制区域数据
        page.textElementAreaVector.clear()
        // 准备待处理的页
        preparePage(page)
        if (page.startWordCursor == null || page.endWordCursor == null) {
            return
        }
        // 判断准备的 page 是否数据有问题
        val labels = prepareTextArea(page)
        // 绘制页面
        drawTextPage(canvas, page, labels)

        // 绘制高亮区域

        // 绘制下划线区域

        // 绘制之前已选中区域
    }

    /**
     * 准备处理页面数据
     */
    private fun preparePage(page: TextPage) {
        page.setSize(0, 0)

        // 如果未准备任何信息，或者已经准备完成，都直接 return
        if (page.pageState == TextPage.State.NONE || page.pageState == TextPage.State.PREPARED) {
            return
        }

        // 根据当前 page 状态做相应绘制操作
        when (page.pageState) {
            // 已知起始光标状态
            TextPage.State.KNOW_START_CURSOR -> {
                if (page.startWordCursor != null) {
                    preparePageInternal(page, page.startWordCursor!!, page.endWordCursor!!)
                }
            }
            // 已知结束光标状态
            TextPage.State.KNOW_END_CURSOR -> {
                if (page.endWordCursor != null) {
                    // preparePageInternal(page,)
                }
            }
        }

        // 更新当前 Page 状态
        page.setPageState(TextPage.State.PREPARED)
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
        var remainAreaHeight = page.pageHeight

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
            val endElementIndex = paragraphCursor.getElementCount() - 1
            // 创建新的行信息
            curLineInfo = TextLineInfo(paragraphCursor, curElementIndex, curCharIndex)

            // 循环遍历 element
            while (curLineInfo!!.endElementIndex <= endElementIndex) {
                // 填充 textLine
                curLineInfo = prepareTextLine(
                    page,
                    paragraphCursor,
                    curLineInfo!!.startElementIndex,
                    curLineInfo!!.startCharIndex,
                    endElementIndex - 1, preLineInfo
                )
                // 剩余高度 = 当前行高度 - 行高
                remainAreaHeight -= (curLineInfo.height + curLineInfo.descent + curLineInfo.vSpaceAfter)
                // 指针新行的末尾
                findWordCursor.moveTo(curLineInfo.endElementIndex, curLineInfo.endCharIndex)
                // 将 line 添加到 page 中
                page.lineInfoList.add(curLineInfo)
            }
            // 获取段落光标当前指向的 element
        } while (hasNextParagraph)

        // 重置文本样式
        resetTextStyle()
    }

    private fun prepareTextLine(
        page: TextPage,
        paragraphCursor: TextParagraphCursor,
        startElementIndex: Int,
        startCharIndex: Int,
        endElementIndex: Int,
        preLineInfo: TextLineInfo?
    ): TextLineInfo {
        // TODO: 是否需要对 LineInfo 进行缓存

        // 创建一个 TextLine
        val curLineInfo = TextLineInfo(paragraphCursor, startElementIndex, startCharIndex)

        // 索引标记
        var curElementIndex = startElementIndex
        var curCharIndex = startCharIndex

        // 是否是段落中的第一行
        var isFirstLine = startElementIndex == 0 && startCharIndex == 0

        if (isFirstLine) {
            // 获取当前元素
            var element = paragraphCursor.getElement(curElementIndex)!!

            // 判断是否是文本样式元素
            while (isStyleElement(element)) {
                // 使用该元素
                applyStyleElement(element)
                // 行起始位置向后移动一位
                ++curElementIndex
                curCharIndex = 0

                // 如果起始位置到末尾，直接退出
                if (curElementIndex == endElementIndex) {
                    break
                }

                // 获取下一个元素
                element = paragraphCursor.getElement(curElementIndex)!!
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
        val maxWidth = page.pageWidth - curTextStyle.getRightIndent(getMetrics())
        // 获取默认的缩进距离
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
        if (curLineInfo.realStartCharIndex === endElementIndex) {
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
        while (curElementIndex <= endElementIndex) {
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
                    lastSpaceWidth = getPaintContext().getSpaceWidth()
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

            // 如果新的宽度，大于最大宽度
            // TODO:这部分暂时没看懂
            if (newWidth > maxWidth &&
                (curLineInfo.endElementIndex != startElementIndex || element is TextWordElement)
            ) {
                break
            }

            // TODO: 这里重置不会发生问题吗

            ++curElementIndex
            curCharIndex = 0

            val previousElement = element

            // 判断是否到达了段落的末尾
            var allowBreak = curElementIndex == endElementIndex

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
        if (curElementIndex <= endElementIndex &&
            (isHyphenationPossible() || curLineInfo.endElementIndex == startElementIndex)
        ) {
            // 获取当前元素
            val element = paragraphCursor.getElement(curElementIndex)

            if (element is TextWordElement) {
                // 宽需要减去当前 element 的 宽
                newWidth -= getWordWidth(element, curCharIndex)
                // 获取最大宽度和当前宽的差值，等于空格的宽度
                var remainSpaceWidth = maxWidth - newWidth
                // 如果当前元素大于 3 字节，并且剩余控件大于 2 倍的空格
                // 或者单个元素独占一行
                if ((element.length > 3 && remainSpaceWidth > 2 * getPaintContext().getSpaceWidth())
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

        if (removeLastSpace) {
            curLineInfo.width -= lastSpaceWidth
            curLineInfo.spaceCount--
        }

        setTextStyle(curTextStyle)

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
        if (curLineInfo.isEndOfParagraph()) {
            curLineInfo.vSpaceAfter = getTextStyle().getSpaceAfter(getMetrics())
        }

        // 加入到缓存中
        if (curLineInfo.endElementIndex != endElementIndex || endElementIndex == curLineInfo.elementCount - 1) {

            mLineInfoCache.put(curLineInfo, curLineInfo)
        }

        // 如果遍历有问题，那直接到末尾
        if (curLineInfo.endElementIndex == startCharIndex && curLineInfo.endElementIndex == startCharIndex) {
            curLineInfo.endElementIndex = paragraphCursor.getElementCount() - 1
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

        val labels = IntArray(page.lineInfoList.size + 1)
        // 遍历行
        for ((index, lineInfo) in page.lineInfoList.withIndex()) {
            lineInfo.adjust(previousInfo)
            // 根据 curLineInfo 信息准备绘制区域
            prepareTextAreaInternal(page, lineInfo, x, y)
            y += lineInfo.height + lineInfo.descent + lineInfo.vSpaceAfter
            labels[index + 1] = page.textElementAreaVector.size()
            previousInfo = lineInfo
        }

        return labels
    }

    private fun prepareTextAreaInternal(page: TextPage, lineInfo: TextLineInfo, x: Int, y: Int) {
        var x = x
        var y = y
        y = (y + lineInfo.height).coerceAtMost(textConfig.getTopMargin() + page.pageWidth - 1)

        val context = getPaintContext()
        val paragraphCursor = lineInfo.paragraphCursor

        setTextStyle(lineInfo.startStyle!!)
        var spaceCounter = lineInfo.spaceCount
        var fullCorrection = 0
        val endOfParagraph = lineInfo.isEndOfParagraph()
        var wordOccurred = false
        var changeStyle = true
        x += lineInfo.leftIndent

        val maxWidth = page.pageWidth
        when (getTextStyle().getAlignment()) {
            TextAlignmentType.ALIGN_RIGHT -> x += maxWidth - getTextStyle().getRightIndent(
                getMetrics()
            ) - lineInfo.width
            TextAlignmentType.ALIGN_CENTER -> x += (maxWidth - getTextStyle().getRightIndent(
                getMetrics()
            ) - lineInfo.width) / 2
            TextAlignmentType.ALIGN_JUSTIFY -> if (!endOfParagraph && paragraphCursor.getElement(
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
        val paragraphIndex = paragraph.curParagraphIndex
        val endElementIndex = lineInfo.endElementIndex
        var charIndex = lineInfo.realStartCharIndex
        var spaceElement: TextElementArea? = null
        run {
            var wordIndex = lineInfo.realStartElementIndex
            while (wordIndex != endElementIndex) {
                // 获取 Element
                val element = paragraph.getElement(wordIndex)
                val width = getElementWidth(element!!, charIndex)
                // 如果是空格元素
                if (element === TextElement.HSpace) {
                    if (wordOccurred && spaceCounter > 0) {
                        val correction = fullCorrection / spaceCounter
                        val spaceLength = context.getSpaceWidth() + correction
                        // 是否是下划线
                        spaceElement = if (getTextStyle().isUnderline()) {
                            TextElementArea(
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
                        wordOccurred = false
                        --spaceCounter
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
                            paragraphIndex,
                            wordIndex,
                            charIndex,
                            length - charIndex,
                            isLastElement = true, // is last in element
                            addHyphenationSign = false, // add hyphenation sign
                            isStyleChange = changeStyle,
                            style = getTextStyle(),
                            element = element,
                            startX = x,
                            startY = x + width - 1,
                            endX = y - height + 1,
                            endY = y + descent
                        )
                    )
                    changeStyle = false
                    wordOccurred = true
                } else if (isStyleElement(element)) {
                    applyStyleElement(element)
                    changeStyle = true
                }
                x += width
                ++wordIndex
                charIndex = 0
            }
        }

        if (!endOfParagraph) {
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
                        paragraphIndex, wordIndex, 0, len,
                        false, // is last in element
                        addHyphenationSign,
                        changeStyle,
                        getTextStyle(),
                        wordElement,
                        x, x + width - 1, y - height + 1, y + descent
                    )
                )
            }
        }
    }

    private fun drawTextPage(canvas: TextCanvas, page: TextPage, labels: IntArray) {
        page.lineInfoList.forEachIndexed { index, textLineInfo ->
            drawTextLine(canvas, page, textLineInfo, labels[index], labels[index + 1])
        }
    }

    /**
     * 绘制文本行
     */
    private fun drawTextLine(
        canvas: TextCanvas,
        page: TextPage,
        lineInfo: TextLineInfo,
        fromArea: Int,
        toArea: Int
    ) {
        val paragraph = lineInfo.paragraphCursor
        var areaIndex = fromArea
        val endElementIndex = lineInfo.endElementIndex
        var charIndex = lineInfo.realStartCharIndex
        val pageAreas = page.textElementAreaVector.areas()
        // TODO:这索引大小有问题吧
        if (toArea > pageAreas.size) {
            return
        }
        // 循环元素
        var wordIndex = lineInfo.realStartElementIndex
        while (wordIndex != endElementIndex && areaIndex < toArea) {
            val element = paragraph.getElement(wordIndex)
            val area = pageAreas.get(areaIndex)
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
                    drawWord(canvas, areaX, areaY, element, charIndex, -1, false,)
                } else if (element === TextElement.HSpace || element === TextElement.NBSpace) {
                    val cw = getPaintContext().getSpaceWidth()
                    var len = 0
                    while (len < area.endX - area.startX) {
                        canvas.drawString(areaX + len, areaY, SPACE, 0, 1, getPaintContext())
                        len += cw
                    }
                }
            }
            ++wordIndex
            charIndex = 0
        }
        if (areaIndex != toArea) {
            val area = pageAreas[areaIndex++]
            if (area.isStyleChange) {
                setTextStyle(area.style)
            }
            val start = if (lineInfo.startElementIndex === lineInfo.endElementIndex)
                lineInfo.startCharIndex
            else 0
            val len = lineInfo.endCharIndex - start
            val word = paragraph.getElement(lineInfo.endElementIndex) as TextWordElement
            val pos = TextFixedPosition(
                lineInfo.paragraphCursor.curParagraphIndex,
                lineInfo.endElementIndex,
                0
            )
            drawWord(
                canvas,
                area.startX,
                area.endY - getPaintContext().getDescent() - getTextStyle().getVerticalAlign(
                    getMetrics()
                ),
                word,
                start,
                len,
                area.addHyphenationSign,
                // 获取文本颜色，这个暂时没想好怎么处理，FBReader 的 Color 和超链接的颜色有关系，我觉得应该从 config 里面拿
            )
        }
    }
}