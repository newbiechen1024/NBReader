package com.example.newbiechen.nbreader.ui.component.widget.page

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.newbiechen.nbreader.ui.component.book.text.processor.TextProcessor
import com.example.newbiechen.nbreader.ui.component.widget.page.anim.*
import com.example.newbiechen.nbreader.uilts.TouchProcessor

/**
 *  author : newbiechen
 *  date : 2019-08-30 16:26
 *  description :页面展示View
 *
 *  TODO:需要设置默认的背景
 */

class PageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), PageBitmapManager.OnPageListener {

    companion object {
        private const val TAG = "PageView"
    }

    private var mPageActionListener: PageActionListener? = null

    // 文本事件处理器
    private var mTextActionProcessor = PageActionProcessor()
        .also { it.addPageActionListener(this::onDispatchAction) }

    // 文本处理器
    private var mTextProcessor: TextProcessor = TextProcessor(this)

    // 点击事件处理器
    private var mTouchProcessor = TouchProcessor(context, mTextActionProcessor)

    // 页面画布管理器
    private var mPageBitmapManager = PageBitmapManager(this)

    // 页面控制器
    private var mPageController: PageController = PageController(this)

    // 当前动画类型
    private var mPageAnimType = PageAnimType.NONE

    // 当前翻页动画
    private var mPageAnim: PageAnimation = NonePageAnimation(this, mPageBitmapManager)

    /**
     * 设置页面动画类型
     */
    fun setPageAnim(type: PageAnimType) {
        if (mPageAnimType != type) {
            mPageAnim = when (type) {
                PageAnimType.NONE -> NonePageAnimation(this, mPageBitmapManager)
                PageAnimType.COVER -> CoverPageAnimation(this, mPageBitmapManager)
                PageAnimType.SLIDE -> SlidePageAnimation(this, mPageBitmapManager)
                PageAnimType.SIMULATION -> SimulationPageAnimation(this, mPageBitmapManager)
            }

            // 重置宽高
            mPageAnim.setup(width, height)
            mPageAnimType = type
        }
    }

    /**
     * 返回文本处理器
     */
    internal fun getTextProcessor(): TextProcessor {
        return mTextProcessor
    }

    internal fun setPageActionListener(pageActionListener: PageActionListener) {
        if (mPageActionListener == null || mPageActionListener != pageActionListener) {
            mPageActionListener = pageActionListener
            mTextActionProcessor.addPageActionListener(pageActionListener)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 设置页面大小
        mPageBitmapManager.setPageSize(w, h)
        // 设置页面动画大小
        mPageAnim.setup(w, h)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mTouchProcessor.onTouchEvent(event!!)
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // 绘制翻页动画
        mPageAnim.draw(canvas!!)
    }

    override fun computeScroll() {
        super.computeScroll()
        // 处理翻页动画，滑动事件
        mPageAnim.computeScroll()
    }

    override fun onPageSizeChange(width: Int, height: Int) {
        // 通知文本处理的视口
        mTextProcessor.setViewPort(width, height)
        // 通知文本事件处理的视口
        mTextActionProcessor.setViewPort(width, height)
    }

    override fun onTurnPage(pageType: PageType) {
        // TODO:如果 TextProcessor 没有被使用的情况下 (setTextModel)，做出这种处理该怎么办？

        // 切换页面
        mTextProcessor.turnPage(pageType)
        // 发送页面改变的通知
        mTextActionProcessor.dispatchAction(TurnPageAction(pageType))
    }

    override fun hasPage(type: PageType): Boolean {
        return mTextProcessor.hasPage(type)
    }

    override fun drawPage(bitmap: Bitmap, type: PageType) {
        // 进行页面绘制
        mTextProcessor.draw(Canvas(bitmap), type)
    }

    /**
     * 接收事件分发的处理
     */
    private fun onDispatchAction(action: Any) {
        when (action) {
            is PressPageAction -> pressPage(action.x, action.y)
            is MovePageAction -> movePage(action.x, action.y)
            is ReleasePageAction -> releasePage(action.x, action.y)
            is TapPageAction -> mPageAnim.startAnim(action.x, action.y)
        }
    }

    private fun pressPage(x: Int, y: Int) {
        mPageAnim.pressPage(x, y)
    }

    private fun movePage(x: Int, y: Int) {
        mPageAnim.movePage(x, y)
    }

    private fun releasePage(x: Int, y: Int) {
        mPageAnim.releasePage(x, y)
    }
}