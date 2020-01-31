package com.example.newbiechen.nbreader.ui.component.book.text.processor

import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextFixedPosition
import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextPage
import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextPosition
import com.example.newbiechen.nbreader.ui.component.book.text.processor.cursor.TextWordCursor
import com.example.newbiechen.nbreader.ui.component.widget.page.PageType
import kotlin.collections.ArrayList
import kotlin.math.min

/**
 *  author : newbiechen
 *  date : 2020-01-30 15:50
 *  description :文本页面控制器
 *
 *  TODO：需要支持章节预加载
 */

// 查找页面的结尾光标
typealias FindPageEndCursorListener = (width: Int, height: Int, startCursor: TextWordCursor) -> TextWordCursor

class TextPageController(
    private val textModel: TextModel,
    private val pageEndCursorListener: FindPageEndCursorListener
) {
    companion object {
        private const val TAG = "TextPageController"
    }

    // 页面宽高
    var pageWidth: Int = 0
        private set
    var pageHeight: Int = 0
        private set

    private var mPrevChapterWrapper: ChapterWrapper? = null
    private var mCurChapterWrapper: ChapterWrapper? = null
    private var mNextChapterWrapper: ChapterWrapper? = null

    // 当前页面在对应页面的索引
    private var mCurPageWrapper: PageWrapper? = null

    /**
     * 设置视口
     */
    fun setViewPort(width: Int, height: Int) {
        if (pageWidth == width && pageHeight == height) {
            return
        }

        pageWidth = width
        pageHeight = height

        if (mCurPageWrapper != null) {
            // 获取当前起始页面的位置
            val curTextPosition = TextFixedPosition(mCurPageWrapper!!.textPage.startWordCursor)
            // 重置数据
            reset()
            // 重新设置当前页面
            setCurrentPage(curTextPosition)
        }
    }

    /**
     * 设置当前页面
     * @param position：页面的访问定位
     */
    fun setCurrentPage(position: TextPosition) {
        check(pageWidth > 0 && pageHeight > 0) {
            "pageWidth or pageHeight mustn't zero"
        }

        val pageWrapper = findPageWrapper(position)

        if (pageWrapper == null) {
            // 重置缓存
            reset()
            // 加载章节
            mCurChapterWrapper = loadChapterPages(position.getChapterIndex())
            // 重新获取页面
            mCurPageWrapper = findPageWrapper(position)!!
        } else {
            // 将 page 设置为当前页
            mCurPageWrapper = pageWrapper

            when (mCurPageWrapper!!.chapterWrapper) {
                mPrevChapterWrapper -> {
                    turnPage(PageType.PREVIOUS)
                }
                mNextChapterWrapper -> {
                    turnPage(PageType.NEXT)
                }
            }
        }
    }

    /**
     * 查找 postion 所属的 chapterWrapper
     */
    private fun findChapterWrapper(position: TextPosition): ChapterWrapper? {
        val chapterIndex = position.getChapterIndex()

        for (chapterWrapper in arrayOf(
            mPrevChapterWrapper,
            mCurChapterWrapper,
            mNextChapterWrapper
        )) {
            if (chapterWrapper != null && chapterWrapper.chapterIndex == chapterIndex) {
                return chapterWrapper
            }
        }
        return null
    }

    /**
     * 查找 position 对应的 Page
     */
    private fun findPageWrapper(position: TextPosition): PageWrapper? {
        // 查找 position 对应的章节
        val chapterWrapper: ChapterWrapper? = findChapterWrapper(position) ?: return null

        // 查找章节中的 page
        val pages = chapterWrapper!!.pages
        for ((index, page) in pages.withIndex()) {
            if (page.endWordCursor > position) {
                return PageWrapper(chapterWrapper, index, page)
            }
        }

        return null
    }

    /**
     * 加载章节页面
     * @param chapterIndex：章节索引
     */
    private fun loadChapterPages(chapterIndex: Int): ChapterWrapper {
        check(chapterIndex >= 0 && chapterIndex < textModel.getChapterCount()) {
            "chapter index out of bounds "
        }

        // 从 textModel 获取章节光标
        val chapterCursor = textModel.getChapterCursor(chapterIndex)

        // TODO:是否存在 chapter 的段落为空的情况，暂时不考虑

        // 根据章节光标，获取单词光标
        val curWordCursor = TextWordCursor(chapterCursor.getParagraphCursor(0))

        // 获取最后一段的起始位置
        val endWordCursor =
            TextWordCursor(chapterCursor.getParagraphCursor(chapterCursor.getParagraphCount() - 1))

        // 跳转到段落的末尾
        endWordCursor.moveToParagraphEnd()

        val pages = ArrayList<TextPage>()

        val pageStartCursor = TextWordCursor(curWordCursor)

        var pageEndCursor: TextWordCursor

        // 如果起始光标不为末尾光标
        while (curWordCursor < endWordCursor) {
            // 获取页面的结尾光标
            pageEndCursor = pageEndCursorListener.invoke(pageWidth, pageHeight, curWordCursor)
            // 将 page 加入到文本列表中
            pages.add(TextPage(pageStartCursor, pageEndCursor))
            // 更新当前光标
            curWordCursor.updateCursor(pageEndCursor)
            // 更新下一个 TextPage 的起始光标
            // 需要这个变量的原因是，防止外部直接使用 curWordCursor ，导致现在拿到的 curWordCursor 被偏移。
            pageStartCursor.updateCursor(pageEndCursor)
        }


        return ChapterWrapper(chapterIndex, pages)
    }

    /**
     * 获取当前页面
     */
    fun getCurrentPage(): TextPage? {
        return mCurPageWrapper?.textPage
    }

    fun getCurentPageSize(): Int {
        return getPageSize(PageType.CURRENT)
    }

    private fun getPageSize(type: PageType): Int {
        val chapterWrapper = when (type) {
            PageType.PREVIOUS -> {
                mPrevChapterWrapper
            }
            PageType.CURRENT -> {
                mCurChapterWrapper
            }
            PageType.NEXT -> {
                mNextChapterWrapper
            }
        }

        return chapterWrapper?.pages?.size ?: 0
    }

    fun hasPage(type: PageType): Boolean {
        return when (type) {
            PageType.PREVIOUS -> {
                hasPrevPage()
            }
            PageType.NEXT -> {
                hasNextPage()
            }
            PageType.CURRENT -> {
                mCurPageWrapper != null
            }
        }
    }

    fun hasPrevPage(): Boolean {
        return if (mCurPageWrapper == null) {
            false
        } else {
            // 获取第一页的起始光标，如果起始光标不在整个文本的起始位置，则表示存在上一页
            !mCurPageWrapper!!.textPage.startWordCursor.isStartOfText()
        }
    }

    fun hasNextPage(): Boolean {
        return if (mCurPageWrapper == null) {
            false
        } else {
            // 获取第一页的起始光标，如果起始光标不在整个文本的结尾位置，则表示存在下一页
            !mCurPageWrapper!!.textPage.endWordCursor.isEndOfText()
        }
    }

    /**
     * 获取上一页
     */
    fun prevPage(): TextPage? {
        val pageWrapper = prevPageWrapper()
        return pageWrapper?.textPage
    }

    private fun prevPageWrapper(): PageWrapper? {
        return if (!hasPrevPage()) {
            null
        } else {
            val pageType: PageType
            val pageIndex: Int

            if (mCurPageWrapper!!.pageIndex > 0) {
                pageType = PageType.CURRENT
                pageIndex = mCurPageWrapper!!.pageIndex - 1
            } else {
                pageType = PageType.PREVIOUS
                // 获取最后一页，直接设置为最大值
                pageIndex = Int.MAX_VALUE
            }

            getPage(pageType, pageIndex)
        }
    }

    /**
     * 获取下一页
     */
    fun nextPage(): TextPage? {
        val pageWrapper = nextPageWrapper()
        return pageWrapper?.textPage
    }

    private fun nextPageWrapper(): PageWrapper? {
        return if (!hasNextPage()) {
            null
        } else {
            val pageType: PageType
            val pageIndex: Int

            if (mCurPageWrapper!!.pageIndex < (mCurChapterWrapper!!.pages.size - 1)) {
                pageType = PageType.CURRENT
                pageIndex = mCurPageWrapper!!.pageIndex + 1
            } else {
                pageType = PageType.NEXT
                pageIndex = 0
            }

            getPage(pageType, pageIndex)
        }
    }

    private fun getPage(pageType: PageType, pageIndex: Int): PageWrapper {
        // 必须在 current page 存在的情况下才能使用
        check(mCurPageWrapper != null) {
            "current page wrapper did not null"
        }

        // curPage 持有的 chapterWrapper
        val curChapterWrapper = mCurPageWrapper!!.chapterWrapper

        val chapterWrapper = when (pageType) {
            PageType.PREVIOUS -> {
                // TODO:异步预加载需要处理
                if (mPrevChapterWrapper == null) {
                    // 获取上一章的章节索引
                    val chapterIndex = curChapterWrapper.chapterIndex - 1
                    mPrevChapterWrapper = loadChapterPages(chapterIndex)
                }
                mPrevChapterWrapper!!
            }

            PageType.CURRENT -> {
                curChapterWrapper
            }

            PageType.NEXT -> {
                // 获取下一章的章节索引
                if (mNextChapterWrapper == null) {
                    val chapterIndex = curChapterWrapper.chapterIndex + 1
                    mNextChapterWrapper = loadChapterPages(chapterIndex)
                }

                mNextChapterWrapper!!
            }
        }

        // 获取页面列表
        val pages = chapterWrapper.pages
        // 选择索引和页面的最小值返回
        val resultPageIndex = min(pages.size - 1, pageIndex)
        // 获取索引
        return PageWrapper(chapterWrapper, resultPageIndex, pages[resultPageIndex])
    }

    /**
     * 进行翻页操作
     */
    fun turnPage(type: PageType) {
        if (mCurPageWrapper == null) {
            return
        }

        // 是否章节需要进行翻章节操作
        var isTurnChapter = false

        // 获取翻页的页面
        val pageWrapper = when (type) {
            PageType.PREVIOUS -> {
                prevPageWrapper()
            }
            PageType.NEXT -> {
                nextPageWrapper()
            }
            PageType.CURRENT -> {
                mCurPageWrapper
            }
        }

        if (pageWrapper != null) {
            // 设置页面为当前页面
            mCurPageWrapper = pageWrapper
            // 检测是否应该翻章处理
            when (type) {
                PageType.PREVIOUS -> {
                    if (pageWrapper.pageIndex == getPageSize(type) - 1) {
                        isTurnChapter = true
                    }
                }
                PageType.NEXT -> {
                    if (pageWrapper.pageIndex == 0) {
                        isTurnChapter = true
                    }
                }
            }

            // 优化 TextPage 内存，防止 TextPage 中 textLine 和 textElement 数据一直被缓存
            val curPages = mCurPageWrapper!!.chapterWrapper.pages
            val pageIndex = mCurPageWrapper!!.pageIndex

            curPages.forEachIndexed { index, textPage ->
                // 只缓存 3 个 Page 的数据
                if (index < pageIndex - 1 || index > pageIndex + 1) {
                    textPage.reset()
                }
            }
        }

        // 进行翻章节操作
        if (isTurnChapter) {
            turnChapter(type)
        }


        // TODO:通知章节回调、或者页面回调
    }

    private fun turnChapter(type: PageType) {
        when (type) {
            PageType.PREVIOUS -> {
                // 向前翻页
                mNextChapterWrapper = mCurChapterWrapper
                mCurChapterWrapper = mPrevChapterWrapper
                mPrevChapterWrapper = null

                // TODO:检测是否进行预加载 (异步处理，需要考虑冲突问题)
                if (hasPrevChapter()) {
                    // 走一个 load 逻辑
                }
            }
            PageType.NEXT -> {
                mPrevChapterWrapper = mCurChapterWrapper
                mCurChapterWrapper = mNextChapterWrapper
                mNextChapterWrapper = null

                // TODO:检测是否进行预加载
                if (hasNextChapter()) {

                }
            }
        }
    }

    private fun hasPrevChapter(): Boolean {
        return if (mCurChapterWrapper == null) {
            false
        } else {
            mCurChapterWrapper!!.chapterIndex - 1 > 0
        }
    }

    private fun hasNextChapter(): Boolean {
        return if (mCurChapterWrapper == null) {
            false
        } else {
            mCurChapterWrapper!!.chapterIndex + 1 < textModel.getChapterCount()
        }
    }

    fun reset() {
        // 重置章节缓冲
        mPrevChapterWrapper = null
        mCurChapterWrapper = null
        mNextChapterWrapper = null
        // 重置页面
        mCurPageWrapper = null

        // TODO:取消预加载处理
    }

    // 章节信息封装
    data class ChapterWrapper(
        val chapterIndex: Int,
        val pages: List<TextPage>
    )

    /**
     * 页面信息封装
     * @param chapterWrapper：Page 属于的 Chapter
     * @param pageIndex：Page 在 Chapter Page 列表的索引
     * @param textPage：Page 内容
     */
    data class PageWrapper(
        val chapterWrapper: ChapterWrapper,
        val pageIndex: Int,
        val textPage: TextPage
    )

    interface OnTextPageListener {
        /**
         * 页面改变通知
         */
        fun onPageChanged(pageIndex: Int, pageCount: Int, progress: Float)

        /**
         * 章节改变
         */
        fun onChapterChanged(chapterIndex: Int)
    }
}