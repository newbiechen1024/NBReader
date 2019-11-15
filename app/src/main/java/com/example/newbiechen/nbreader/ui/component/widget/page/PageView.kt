package com.example.newbiechen.nbreader.ui.component.widget.page

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.newbiechen.nbreader.ui.component.book.text.processor.TextProcessor
import com.example.newbiechen.nbreader.ui.component.widget.page.anim.*
import com.example.newbiechen.nbreader.uilts.LogHelper
import com.example.newbiechen.nbreader.uilts.TouchProcessor
import java.util.concurrent.Executors

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
    private var mPageAnimType = PageAnimType.SIMULATION

    // 当前翻页动画
    private var mPageAnim: PageAnimation = SimulationPageAnimation(this, mPageBitmapManager)

    // 单线程池，用于处理下一页的预加载逻辑
    private var mSingleExecutor = Executors.newSingleThreadExecutor()

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
     * 清空页面缓存
     */
    fun resetCache() {
        mPageBitmapManager.resetPages()
    }

    fun getPageController(): PageController = mPageController

    /**
     * 返回文本处理器
     */
    internal fun getTextProcessor(): TextProcessor {
        return mTextProcessor
    }

    /**
     * 设置页面行为监听
     */
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

        LogHelper.i(TAG,"computeScroll: ")
        // 处理翻页动画，滑动事件
        mPageAnim.computeScroll()

        super.computeScroll()
    }



    override fun onPageSizeChange(width: Int, height: Int) {
        // 通知文本处理的视口
        mTextProcessor.setViewPort(width, height)
        // 通知文本事件处理的视口
        mTextActionProcessor.setViewPort(width, height)
    }

    override fun onTurnPage(type: PageType) {
        // TODO:如果 TextProcessor 没有被使用的情况下 (setTextModel)，做出这种处理该怎么办？
        LogHelper.i(TAG, "onTurnPage: $type")

        // 切换页面
        mTextProcessor.turnPage(type)
        // 发送页面改变的通知
        mTextActionProcessor.dispatchAction(TurnPageAction(type))

        // 如果切换页面，则根据切换类型预加载页面
        // 因为，PageBitmap 实现了三张图的缓存，因此翻到上一页，则 preBitmap 空缺。
        // TODO:逻辑放在这里有点不知所云，不懂原理很难理解。(看看有没有好的情况处理这个问题)
        if (hasPage(type)) {
            mSingleExecutor.execute {
                LogHelper.i(TAG, "onTurnPage: $type")
                mTextProcessor.preparePage(type)
            }
        }
    }

    override fun hasPage(type: PageType): Boolean {
        val hasPage = mTextProcessor.hasPage(type)
        LogHelper.i(TAG, "hasPage: $hasPage")
        return hasPage
    }

    override fun drawPage(bitmap: Bitmap, type: PageType) {
        LogHelper.i(TAG, "drawPage: $type")
        // 进行页面绘制
        mTextProcessor.draw(Canvas(bitmap), type)

        // TODO:放在这里总感觉不太符合逻辑。。
        // 如果绘制的是当前页，预加载下一页
        if (type == PageType.CURRENT && hasPage(PageType.NEXT)) {
            mSingleExecutor.execute {
                mTextProcessor.preparePage(PageType.NEXT)
            }
        }
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