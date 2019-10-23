package com.example.newbiechen.nbreader.ui.component.widget.page

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.ui.component.book.BookModel
import com.example.newbiechen.nbreader.ui.component.book.text.processor.TextProcessor
import com.example.newbiechen.nbreader.uilts.TouchProcessor

/**
 *  author : newbiechen
 *  date : 2019-08-30 16:20
 *  description :页面控制器，控制、加载、翻页、绘制、点击事件
 *
 *  1. 解决书籍解析问题。
 *  2. 绘制书籍。
 *  3. 根据点击的位置处理事件分发。
 *
 *  比较好的话，就是将 PageController 分为：IPageController 接口 -> PageTextController -> PageTouchController
 *  不过我还没有全部理解，所以比较好的方式还是先先到一起，然后慢慢分割。
 */

typealias PageActionListener = (action: Any) -> Unit

class PageController(private var pageView: PageView) : TouchProcessor.OnTouchListener, PageManager.OnPageListener {

    private var mPageActionList = mutableSetOf<PageActionListener>()

    private var mPageWidth = 0
    private var mPageHeight = 0
    private var mMenuRect = Rect()

    private var mTextProcessor: TextProcessor = TextProcessor()

    companion object {
        private const val TAG = "PageController"
    }

    fun setBookModel(bookModel: BookModel) {
        if (bookModel.textModel == null) {
            return
        }

        // 设置书籍
        mTextProcessor.setTextModel(bookModel.textModel!!)

        // 重新绘制
        pageView.invalidate()
    }

    fun addPageActionListener(pageAction: PageActionListener) {
        mPageActionList.add(pageAction)
    }

    fun removePageActionListener(pageAction: PageActionListener) {
        mPageActionList.remove(pageAction)
    }

    override fun onPress(x: Int, y: Int) {
        dispatchAction(PressPageAction(x, y))
    }

    override fun onMove(x: Int, y: Int) {
        dispatchAction(MovePageAction(x, y))
    }

    override fun onRelease(x: Int, y: Int) {
        dispatchAction(ReleasePageAction(x, y))
    }

    override fun onLongPress(x: Int, y: Int) {

    }

    override fun onMoveAfterLongPress(x: Int, y: Int) {
    }

    override fun onReleaseAfterLongPress(x: Int, y: Int) {
    }

    override fun onSingleTap(x: Int, y: Int) {
        val action: Any = if (mMenuRect.contains(x, y)) {
            ReadMenuAction()
        } else {
            // 检测当前点击区域是否
            TapPageAction(x, y)
        }

        dispatchAction(action)
    }

    override fun onDoubleTap(x: Int, y: Int) {
    }

    override fun onCancelTap() {
        // TODO:处理 cancel 的情况
    }

    private fun dispatchAction(action: Any) {
        mPageActionList.forEach {
            it(action)
        }
    }

    override fun onPageSizeChange(w: Int, h: Int) {
        mPageWidth = w
        mPageHeight = h
        mMenuRect.set((w / 5), (h / 3), w * 4 / 5, h * 2 / 3)
    }

    override fun onPageTurn(pageType: PageType) {
        // 处理翻页
        mTextProcessor.turnPage(pageType)
        // 发送页面翻页事件
        dispatchAction(TurnPageAction(pageType))
    }

    override fun hasPage(type: PageType): Boolean {
        // 判断是否页面存在
        return mTextProcessor.hasPage(type)
    }

    override fun drawPage(bitmap: Bitmap, type: PageType) {
        val canvas = Canvas(bitmap)
        val colorId = when (type) {
            PageType.NEXT -> pageView.context.resources.getColor(R.color.read_background_1)
            else -> pageView.context.resources.getColor(R.color.read_background_2)
        }
        canvas.drawColor(colorId)
    }
}