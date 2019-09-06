package com.example.newbiechen.nbreader.ui.component.widget.page

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.newbiechen.nbreader.ui.component.widget.page.anim.*
import com.example.newbiechen.nbreader.uilts.LogHelper
import com.example.newbiechen.nbreader.uilts.TouchProcessor

/**
 *  author : newbiechen
 *  date : 2019-08-30 16:26
 *  description :页面展示View
 */

class PageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "PageView"
    }

    private var mPageController = PageController(context).also {
        it.addPageActionListener(this::onDispatchAction)
    }

    // 点击事件处理器
    private var mTouchProcessor = TouchProcessor(context, mPageController)
    // 页面管理器
    private var mPageManager = PageManager(mPageController)
    // 当前动画类型
    private var mPageAnimType = PageAnimType.NONE
    // 当前翻页动画
    private var mPageAnim: PageAnimation = NonePageAnimation(this, mPageManager)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 设置页面大小
        mPageManager.setPageSize(w, h)
        LogHelper.i(TAG, "onSizeChanged: $w  $h")
        mPageAnim.setup(w, h)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mTouchProcessor.onTouchEvent(event!!)
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // 绘制动画
        mPageAnim.draw(canvas!!)
    }

    /**
     * 设置页面动画类型
     */
    fun setPageAnim(type: PageAnimType) {
        if (mPageAnimType != type) {
            mPageAnim = when (type) {
                PageAnimType.NONE -> NonePageAnimation(this, mPageManager)
                PageAnimType.COVER -> CoverPageAnimation(this, mPageManager)
                PageAnimType.SLIDE -> SlidePageAnimation(this, mPageManager)
                PageAnimType.SIMULATION -> SimulationPageAnimation(this, mPageManager)
            }

            // 重置宽高
            mPageAnim.setup(width, height)
            mPageAnimType = type
        }
    }

    fun addPageActionListener(pageActionListener: PageActionListener) {
        mPageController.addPageActionListener(pageActionListener)
    }

    fun removePageActionListener(pageActionListener: PageActionListener) {
        mPageController.removePageActionListener(pageActionListener)
    }

    override fun computeScroll() {
        super.computeScroll()
        // 处理翻页动画，滑动事件
        mPageAnim.computeScroll()
    }

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