package com.newbiechen.nbreader.ui.component.widget.page.anim

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Scroller
import com.newbiechen.nbreader.ui.component.widget.page.PageType
import com.newbiechen.nbreader.ui.component.widget.page.text.TextPageManager
import com.newbiechen.nbreader.uilts.LogHelper
import kotlin.collections.ArrayList
import kotlin.math.min

/**
 *  author : newbiechen
 *  date : 2020/3/3 10:48 PM
 *  description :滚动动画
 */
class ScrollPageAnimation(view: View, pageManager: TextPageManager) {
    companion object {
        private const val TAG = "ScrollPageAnimation"

        // 滑动追踪的时间
        private const val VELOCITY_DURATION = 1000
    }

    val isRunning: Boolean
        get() = mStatus != Status.None

    private val mView = view
    private val mPageManager = pageManager

    // 动画滑动器
    private val mScroller = Scroller(view.context, LinearInterpolator())

    // 速度检测器
    private var mVelocity: VelocityTracker? = null

    // 创建两个页面布局
    private val mPageLayoutArray = Array(2) {
        PageLayout()
    }


    // 起始点
    private var mStartX: Int = 0
    private var mStartY: Int = 0

    // 触碰点
    private var mTouchX: Int = 0
    private var mTouchY: Int = 0

    // 上一个触碰点
    private var mLastX: Int = 0
    private var mLastY: Int = 0

    // 视图的宽高
    private var mViewWidth = 0
    private var mViewHeight = 0

    private var mStatus = Status.None

    /**
     * 设置宽高
     */
    fun setup(w: Int, h: Int) {
        if (w == 0 || h == 0 || mViewWidth == w || mViewHeight == h) {
            return
        }

        mViewWidth = w
        mViewHeight = h

        // 通知页面管理器
        mPageManager.onPageSizeChanged(w, h)

        // 通知取消动画
        abortAnim()

        // 重置页面高度
        mPageLayoutArray.forEach {
            it.setHeight(h)
        }

        // 进行重新布局
        layout()
    }

    // 触碰页面
    fun pressPage(event: MotionEvent) {
        // 如果当前正在执行动画，先取消动画
        when (mStatus) {
            Status.Fling -> abortAnim()
            else -> {
                // 不处理
            }
        }

        setStartPoint(event.x.toInt(), event.y.toInt())

        // 初始化速度追踪器
        if (mVelocity == null) {
            mVelocity = VelocityTracker.obtain()
        }

        mVelocity!!.addMovement(event)
        // 设置为按下状态
        mStatus = Status.ManualPress
    }

    private fun setStartPoint(x: Int, y: Int) {
        // 设置起始点
        mStartX = x
        mStartY = y
        // 设置触碰点
        mTouchX = x
        mTouchY = y
    }

    // 滑动页面
    fun movePage(event: MotionEvent) {
        when (mStatus) {
            Status.None, Status.Fling -> pressPage(event)
        }

        val x = event.x.toInt()
        val y = event.y.toInt()

        // 上一触碰点
        setTouchPoint(x, y)

        // 设置状态
        mStatus = Status.ManualMove

        mVelocity!!.addMovement(event)
        // 计算当前速度
        mVelocity!!.computeCurrentVelocity(VELOCITY_DURATION)
        // 进行刷新
        mView.postInvalidate()
    }

    private fun setTouchPoint(x: Int, y: Int) {
        mLastX = mTouchX
        mLastY = mTouchY

        mTouchX = x
        mTouchY = y
    }

    // 释放页面
    fun releasePage(event: MotionEvent) {
        when (mStatus) {
            Status.None, Status.Fling -> pressPage(event)
        }

        setTouchPoint(event.x.toInt(), event.y.toInt())

        // 删除检测器
        mVelocity!!.addMovement(event)

        // 开启动画
        startAnim()

        mVelocity!!.recycle()
        mVelocity = null
    }

    fun cancelPage() {
        if (mVelocity != null) {
            // 删除检测器
            mVelocity!!.recycle()
            mVelocity = null
        }

        finishAnim()
    }

    // 获取竖直滑动的距离
    private fun getScrollY(): Int {
        return if (isRunning) mTouchY - mLastY else 0
    }

    /**
     * 进行布局操作
     */
    private fun layout() {
        val scrollY = getScrollY()
        // 进行填充布局操作
        if (scrollY > 0) {
            fillUp(scrollY)
        } else {
            fillDown(scrollY)
        }
    }

    /**
     * 向上滑动，填充底部空白区域
     */
    private fun fillDown(scrollY: Int) {
        var pageLayout: PageLayout
        // 是否进行翻页操作
        var hasTurnPage = false

        mPageLayoutArray.forEach {
            if (it.type == null) {
                return@forEach
            }
            // 如果 scrollY >= viewHeight 整个逻辑就会出问题。(默认这种情况会不发生)
            // 布局进行偏移操作
            it.offset(scrollY)

            // 检测当前 layout 是否超出显示区域
            if (it.bottom <= 0) {
                it.reset()
                // 如果顶部被删除，则需要 turnPage(true)，通知翻页，说明上一页没了，则下一页就是当前页了
                mPageManager.turnPage(true)
                // 表示进行翻页操作
                hasTurnPage = true
            }
        }

        // 更新页面布局信息
        if (hasTurnPage) {
            // 进行翻页操作
            turnPageLayout(PageType.NEXT)
        }

        // 检测当前页是否存在，如果不存在则添加一个当前页布局。
        if (getPageLayout(PageType.CURRENT) == null && mPageManager.hasPage(PageType.CURRENT)) {
            val newPageLayout = getScrapPageLayout()!!
            newPageLayout.type = PageType.CURRENT
        }

        val pageBottom = getPageBottom()

        // 获取填充区域
        val fillArea = if (pageBottom == null) {
            mViewHeight
        } else {
            0.coerceAtLeast(mViewHeight - pageBottom)
        }

        // 检测是否存在空白区域
        if (fillArea > 0) {
            // 检测是否存在下一页
            if (mPageManager.hasPage(PageType.NEXT)) {
                // 假设，一个 layout 就能够填充满 spaceArea
                val activeLayout = getScrapPageLayout()!!
                activeLayout.type = PageType.NEXT
                // 滑动到指定位置
                activeLayout.offset(mViewHeight - fillArea)
            } else {
                // 如果存在，则设置为屏幕位置
                if (hasActivePageLayout()) {
                    clearPageLayout()
                    // 清空列表
                    // 获取一个能用的 active
                    pageLayout = getScrapPageLayout()!!
                    pageLayout.type = PageType.CURRENT
                }
                // 无法继续滑动了，取消动画
                abortAnim()
            }
        }
    }

    /**
     * 向下滑动，填充顶部空白区域
     */
    private fun fillUp(scrollY: Int) {
        var pageLayout: PageLayout
        var hasTurnPage = false

        // 为每个 layout 加上滑动距离，并检测存在越界的情况 (top >= viewHeight)，如果越界就移除该 item
        mPageLayoutArray.forEach {
            if (it.type == null) {
                return@forEach
            }

            // 布局进行偏移操作
            it.offset(scrollY)

            // 检测当前 layout 是否超出显示区域
            if (it.top >= mViewHeight) {
                it.reset()
                // 只有在如下情况，cur page 才能自动转换为 nextPage
                if (mPageManager.hasPage(PageType.PREVIOUS)) {
                    // 通知换页，说明下一页被删除了，则向前翻页。则上一页就变成当前页，当前页变成了下一页。
                    mPageManager.turnPage(false)
                    hasTurnPage = true
                }
            }
        }

        // 请求翻页操作
        if (hasTurnPage) {
            // 进行翻页操作
            turnPageLayout(PageType.PREVIOUS)
        }

        // 检测当前页是否存在，如果不存在则添加当前页。
        if (getPageLayout(PageType.CURRENT) == null && mPageManager.hasPage(PageType.CURRENT)) {
            val pageTop = getPageTop()

            val newPageLayout = getScrapPageLayout()

            newPageLayout!!.type = PageType.CURRENT

            if (pageTop != null) {
                newPageLayout.offset(pageTop - mViewHeight)
            }
        }

        val pageTop = getPageTop()

        // 填充区域
        val fillArea = if (pageTop == null) {
            mViewHeight
        } else {
            0.coerceAtLeast(pageTop)
        }
        if (fillArea > 0) {
            // 检测是否存在上一页
            if (mPageManager.hasPage(PageType.PREVIOUS)) {
                // 假设，一个 layout 就能够填充满 spaceArea
                // 如果存在空白区域，则进行填充
                val newPageLayout = getScrapPageLayout()!!
                newPageLayout.type = PageType.PREVIOUS
                // 滑动到指定位置
                newPageLayout.offset(fillArea - mViewHeight)

                // 通知换页，说明下一页被删除了，则向前翻页。则上一页就变成当前页，当前页变成了下一页。
                mPageManager.turnPage(false)
                turnPageLayout(PageType.PREVIOUS)
            } else {
                if (hasActivePageLayout()) {
                    clearPageLayout()
                    // 清空列表
                    // 获取一个能用的 active
                    pageLayout = getScrapPageLayout()!!
                    pageLayout.type = PageType.CURRENT
                }
                // 无法继续滑动了，取消动画
                abortAnim()
            }
        }
    }

    private fun getPageTop(): Int? {
        var resultTop: Int? = null

        mPageLayoutArray.forEach {
            if (it.type == null) {
                return@forEach
            }
            if (resultTop == null) {
                resultTop = it.top
            } else {
                if (it.top < resultTop!!) {
                    resultTop = it.top
                }
            }
        }
        return resultTop
    }

    private fun getPageBottom(): Int? {
        var resultBottom: Int? = null
        mPageLayoutArray.forEach {
            if (it.type == null) {
                return@forEach
            }

            if (resultBottom == null) {
                resultBottom = it.bottom
            } else {
                if (it.bottom > resultBottom!!) {
                    resultBottom = it.bottom
                }
            }
        }
        return resultBottom
    }


    /**
     * 根据页面类型获取布局
     */
    private fun getPageLayout(type: PageType): PageLayout? {
        return mPageLayoutArray.firstOrNull {
            it.type == type
        }
    }

    /**
     * 获取被废弃的 layout
     */
    private fun getScrapPageLayout(): PageLayout? {
        return mPageLayoutArray.firstOrNull {
            it.type == null
        }
    }

    private fun hasActivePageLayout(): Boolean {
        for (pageLayout in mPageLayoutArray) {
            if (pageLayout.type != null) {
                return true
            }
        }
        return false
    }

    private fun clearPageLayout() {
        for (pageLayout in mPageLayoutArray) {
            pageLayout.reset()
        }
    }

    /**
     * 对 Page 进行翻页操作
     */
    private fun turnPageLayout(type: PageType) {
        mPageLayoutArray.forEach {
            when (type) {
                PageType.PREVIOUS -> {
                    it.type = it.type?.getNext()
                }
                PageType.NEXT -> {
                    it.type = it.type?.getPrevious()
                }
            }
        }
    }

    /**
     * 绘制操作
     */
    fun draw(canvas: Canvas) {
        // 进行匹配操作
        if (isRunning) {
            drawMove(canvas)
        } else {
            drawStatic(canvas)
        }
    }

    /**
     * 绘制动态图像
     */
    private fun drawMove(canvas: Canvas) {
        // 绘制时，首先进行布局
        layout()
        // 拿到最终的数据，进行绘制操作
        drawStatic(canvas)
    }

    /**
     * 绘制静态图像
     */
    private fun drawStatic(canvas: Canvas) {
        // 如果布局为空，则会直接发起请求操作
        if (!hasActivePageLayout()) {
            layout()
        }

        canvas.save()
        //裁剪显示区域
        canvas.clipRect(0, 0, mViewWidth, mViewHeight)

        mPageLayoutArray.forEach { pageLayout ->
            canvas.save()
            canvas.translate(0f, pageLayout.top.toFloat())

            if (pageLayout.type == PageType.CURRENT) {
                canvas.drawPicture(mPageManager.getPage(PageType.CURRENT))
            }

            if (pageLayout.type == PageType.NEXT) {
                canvas.drawPicture(mPageManager.getPage(PageType.NEXT))
            }
            canvas.restore()
        }
        canvas.restore()
    }

    /**
     * 启动动画
     */
    fun startAnim() {
        mScroller.fling(
            0, mTouchY, 0, mVelocity!!.yVelocity.toInt()
            , 0, 0, Int.MAX_VALUE * -1, Int.MAX_VALUE
        )
        mStatus = Status.Fling
    }

    /**
     * 取消动画
     */
    fun abortAnim() {
        if (!mScroller.isFinished) {
            mScroller.abortAnimation()
        }
        // 滑动结束
        finishAnim()
    }

    /**
     * 滑动结束
     */
    private fun finishAnim() {
        mStatus = Status.None
        mView.postInvalidate()
    }

    /**
     * 计算滑动
     */
    fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            val x = mScroller.currX
            val y = mScroller.currY

            setTouchPoint(x, y)

            // 如果达到最终点，则结束滑动
            if (mScroller.finalX == x && mScroller.finalY == y) {
                finishAnim()
            } else {
                mView.postInvalidate()
            }
        }
    }

    // 页面布局
    private class PageLayout {
        // Page Top 距离 ViewPort Top 的位置
        var top = 0
            private set

        // Page Bottom 距离 ViewPort Top 的位置
        var bottom = 0
            private set

        // 当前布局针对的页面类型
        var type: PageType? = null

        var height = 0
            private set

        /**
         * 设置高度
         */
        fun setHeight(height: Int) {
            this.height = height
            reset()
        }

        /**
         * 偏移操作
         */
        fun offset(y: Int) {
            top += y
            bottom += y
        }

        /**
         * 重置宽高
         */
        fun reset() {
            top = 0
            bottom = height
            type = null
        }

        override fun toString(): String {
            return "PageLayout(height=$height, top=$top, bottom=$bottom)"
        }

        /*  这些参数暂时用不到
        // bitmap 可使用范围
        var srcRect: Rect? = null
        // bitmap 绘制范围
        var destRect: Rect? = null*/
        // bitmap 顶部与视口顶部的距离
    }

    // 动画状态
    private enum class Status {
        None, // 无状态
        ManualPress, // 手动按下
        ManualMove, // 手动移动
        Fling, // 滑动状态
    }
}

