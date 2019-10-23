package com.example.newbiechen.nbreader.ui.component.book.text.processor

import android.graphics.Canvas
import com.example.newbiechen.nbreader.ui.component.book.text.TextModel
import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextLineInfo
import com.example.newbiechen.nbreader.ui.component.widget.page.PageType

/**
 *  author : newbiechen
 *  date : 2019-10-20 18:50
 *  description :文本处理器，处理与绘制页面
 */

class TextProcessor {
    // 上一页文本
    private var mPrePage = TextPage()
    // 当前页文本
    private var mCurPage = TextPage()
    // 下一页文本
    private var mNextPage = TextPage()

    private var mTextModel: TextModel? = null

    // 段落光标管理器
    private var mCursorManager: TextCursorManager? = null

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
        // 切换页面

        // 是否需要判断 TextModel
    }

    /**
     * 是否页面存在
     */
    fun hasPage(index: PageType): Boolean {
        return true
    }

    /**
     * 绘制页面，drawPage 只能绘制当前页的前一页和后一页。所以继续绘制下一页需要先进行
     * @see turnPage 翻页操作
     * @param canvas:被绘制的画布
     * @param pageType:绘制的页面类型
     */
    fun drawPage(canvas: Canvas, pageType: PageType) {
        // 如果禁止绘制直接 return

        // 设置将要被处理的 Page
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

        // 准备带处理的页
        preparePage(page)

        // 判断准备的 page 是否数据有问题

        // 对数据进行处理

    }

    /**
     * 准备处理页面数据
     */
    private fun preparePage(page: TextPage) {
        // TODO:设置页面的宽高 ==> 暂时没想好配置参数通过什么方式添加进来，创建一个 Config 对象???
        page.setSize(0, 0)
        // 如果未准备任何信息，或者已经准备完成，都直接 return
        if (page.pageState == TextPage.State.NONE || page.pageState == TextPage.State.PREPARED) {
            return
        }

        when (page.pageState) {
            TextPage.State.KNOW_START_CURSOR -> {
                if (page.startWordCursor != null) {
                    preparePageInternal(page, page.startWordCursor!!, page.endWordCursor!!)
                }
            }

            TextPage.State.KNOW_END_CURSOR -> {
                if (page.endWordCursor != null) {
                    // preparePageInternal(page,)
                }
            }
        }

        // 设置当前页面状态
        page.setPageState(TextPage.State.PREPARED)
    }

    /**
     * 根据光标，填充 page 信息
     */
    private fun preparePageInternal(page: TextPage, startWordCursor: TextWordCursor, endWordCursor: TextWordCursor) {
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
            // 上一段行信息
            val preLineInfo = curLineInfo
            // 获取段落光标
            val paragraphCursor = findWordCursor.getParagraphCursor()
            val curElementIndex = findWordCursor.getElementIndex()
            val curCharIndex = findWordCursor.getCharIndex()
            val elementCount = paragraphCursor.getElementCount()

            curLineInfo = TextLineInfo(paragraphCursor, curElementIndex, curCharIndex)

            // 对填充一行的数据
            while (curLineInfo!!.endElementIndex < elementCount) {
                curLineInfo = prepareTextLine(
                    page,
                    paragraphCursor,
                    curLineInfo!!.startElementIndex,
                    curLineInfo!!.startCharIndex,
                    elementCount, preLineInfo
                )

                remainAreaHeight -= (curLineInfo.height + curLineInfo.descent + curLineInfo.vSpaceAfter)
                // 指针指向行末尾
                findWordCursor.moveTo(curLineInfo.endElementIndex, curLineInfo.endCharIndex)
                // 将 line 添加到 page 中
                page.lineInfoList.add(curLineInfo)

            }

            // 获取段落光标当前指向的 element
        } while (hasNextParagraph)

        // 重置文本样式
    }

    private fun prepareTextLine(
        page: TextPage,
        paragraphCursor: TextParagraphCursor,
        startElementIndex: Int,
        startCharIndex: Int,
        elementCount: Int,
        preLineInfo: TextLineInfo?
    ): TextLineInfo {
        // TODO: 是否需要对 LineInfo 进行缓存

        // 创建一个 TextLine
        val lineInfo = TextLineInfo(paragraphCursor, startElementIndex, startCharIndex)

        var curElementIndex = startElementIndex
        var curCharIndex = startCharIndex
        // 是否是段落中的第一行
        var isFirstLine = startElementIndex == 0 && startCharIndex == 0

        if (isFirstLine) {
            // 获取样式信息
        }

        // 需要根据样式信息与配置信息确定 LineInfo 信息


        // 遍历 element 填充 TextLine

        while (curCharIndex < elementCount) {
            // 进行处理操作
        }

        /**
         * 如果遍历有问题，那直接到末尾？？？
         */
        if (lineInfo.endElementIndex == startCharIndex && lineInfo.endElementIndex == startCharIndex) {
            lineInfo.endElementIndex = paragraphCursor.getElementCount() - 1
            lineInfo.endCharIndex = 0
        }

        return lineInfo
    }

    /**
     * 根据 TextLine 设置文本绘制区域
     */
    private fun prepareTextArea() {

    }

    /**
     * 绘制文本行
     */
    private fun drawTextLine() {

    }

    /**
     * 根据结尾光标，查找页面对应的起始光标
     */
    private fun findPageStartCursor(page: TextPage, cursor: TextWordCursor) {

    }

}