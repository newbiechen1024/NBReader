package com.newbiechen.nbreader.ui.component.widget.page.anim

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.LinearInterpolator
import android.widget.Scroller
import com.newbiechen.nbreader.ui.component.widget.page.PageType
import com.newbiechen.nbreader.ui.component.widget.page.text.TextPageManager
import com.newbiechen.nbreader.uilts.LogHelper
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

/**
 *  author : newbiechen
 *  date : 2020/3/3 10:48 PM
 *  description :滚动动画
 * TODO：整套逻辑太复杂了，需要优化
 */
class ScrollPageAnimation(view: View, pageManager: TextPageManager) {
    companion object {
        private const val TAG = "ScrollPageAnimation"

        // 滑动追踪的时间
        private const val VELOCITY_DURATION = 1500
    }

    val isRunning: Boolean
        get() = mStatus != Status.None

    private val mView = view
    private val mPageManager = pageManager

    // 动画滑动器
    private val mScroller = Scroller(view.context, LinearInterpolator())

    // 速度检测器
    private var mVelocity: VelocityTracker? = null

    // 元素列表 (直接使用 curLayout 和 nextLayout，然后有个 ActiveLayout)
    private val mActiveLayouts: ArrayList<PageLayout> = ArrayList(2)
    private val mScrapLayouts: ArrayDeque<PageLayout> = ArrayDeque(2)

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

    private var isMove = false

    private var mStatus = Status.None

    private val MIN_SCROLL_SLOP = ViewConfiguration.get(mView.context).scaledTouchSlop

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

        // TODO:暂时销毁所有 itemLayout，
        // TODO:实际应该只需要调整一下 active layout 的 bottom 的高度就行了，销毁 mScrap

        // 进行重新布局
        layout()
    }
    // TODO:这个改成 press、move、release

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
        isMove = false
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

        // 判断是否大于最小滑动值。
        if (!isMove) {
            isMove = abs(mStartX - x) > MIN_SCROLL_SLOP || abs(mStartY - y) > MIN_SCROLL_SLOP
        }

        if (isMove) {
            // TODO：是否支持设置滑动上限？
            // 计算当前速度
            mVelocity!!.computeCurrentVelocity(VELOCITY_DURATION)
            // 进行刷新
            mView.postInvalidate()
        }

        // 设置状态
        mStatus = Status.ManualMove
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

        // 开启动画
        startAnim()
    }

    fun cancelPage() {
        if (mVelocity != null) {
            // 删除检测器
            mVelocity!!.recycle()
            mVelocity = null
        }

        finishAnim()
    }

    /**
     *  点击事件
     *  TODO:这个需要删掉，改用 press 的逻辑
     */
    fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isMove = false
                // 设置起始点
                setStartPoint(x, y)
                // 停止动画
                abortAnim()
            }
            MotionEvent.ACTION_MOVE -> {

                // 设置触碰点
                setTouchPoint(x, y)

                // 判断是否大于最小滑动值。
                val slop = ViewConfiguration.get(mView.context).scaledTouchSlop
                if (!isMove) {
                    isMove = abs(mStartX - event.x) > slop || abs(mStartY - event.y) > slop
                }

                if (isMove) {
                    // 计算当前速度
                    mVelocity!!.computeCurrentVelocity(VELOCITY_DURATION)
                    // 进行刷新
                    mView.postInvalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                // 设置触碰点
                setTouchPoint(x, y)

                // 开启动画
                startAnim()

                // 删除检测器
                mVelocity!!.recycle()
                mVelocity = null
            }
            MotionEvent.ACTION_CANCEL -> {
                // if velocityTracker won't be used should be recycled
                mVelocity!!.recycle()
                mVelocity = null
                finishAnim()
            }
        }
        return true
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
        val activeItr = mActiveLayouts.iterator()
        var pageLayout: PageLayout
        // 是否进行翻页操作
        var hasTurnPage = false

        // 为每个 active layout 加上滑动距离。
        while (activeItr.hasNext()) {
            pageLayout = activeItr.next()
            // 滑动 layout
            // 如果 scrollY >= viewHeight 整个逻辑就会出问题。(默认这种情况会不发生)
            pageLayout.scrollY(scrollY)
            // 检测存在越界的情况 (bottom <= 0)，就移除该 item
            if (pageLayout.bottom <= 0) {
                // 从Active中移除
                activeItr.remove()
                // 添加到废弃的View中
                mScrapLayouts.add(pageLayout)
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

        // 检测当前页是否存在，如果不存在则添加一个当前页。
        if (getCurrentPageLayout() == null && mPageManager.hasPage(PageType.CURRENT)) {
            val newPageLayout = getScrapLayout()
            newPageLayout.type = PageType.CURRENT
            mActiveLayouts.add(newPageLayout)
        }

        // 获取填充区域
        val fillArea = when {
            mActiveLayouts.isEmpty() -> {
                // TODO:如果列表不存在可以用的 item，则此次滑动认为无效
                // mViewHeight + scrollY
                mViewHeight
            }
            mActiveLayouts.last().bottom < mViewHeight -> {
                // 如果顶部被删除，那么剩下的只有一个 active Layout。
                // 那么 viewHeight - bottom 就是剩余空间
                mViewHeight - mActiveLayouts[0].bottom
            }
            else -> {
                0
            }
        }

        // 检测是否存在空白区域
        if (fillArea > 0) {
            // 检测是否存在下一页
            if (mPageManager.hasPage(PageType.NEXT)) {
                // 假设，一个 layout 就能够填充满 spaceArea
                val activeLayout = getScrapLayout()
                activeLayout.type = PageType.NEXT
                // 滑动到指定位置
                activeLayout.scrollY(mViewHeight - fillArea)
                // 添加到 active 中
                mActiveLayouts.add(activeLayout)
            } else {
                // 如果不存在，需要将
                if (mActiveLayouts.isNotEmpty()) {
                    // 将所有存活的 layout 全部废弃
                    addScrapLayout(mActiveLayouts)
                    // 清空列表
                    mActiveLayouts.clear()
                    // 获取一个能用的 active
                    pageLayout = getScrapLayout()
                    pageLayout.type = PageType.CURRENT
                    // 加入到存活列表中
                    mActiveLayouts.add(pageLayout)
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
        // TODO：逻辑太难看了，需要重构..
        val activeItr = mActiveLayouts.iterator()
        var pageLayout: PageLayout
        var hasTurnPage = false

        // 为每个 layout 加上滑动距离，并检测存在越界的情况 (top >= viewHeight)，如果越界就移除该 item
        // 如果顶部被删除，则需要 turnPage() PREV，翻页。
        while (activeItr.hasNext()) {
            pageLayout = activeItr.next()
            // 滑动 layout
            pageLayout.scrollY(scrollY)
            // 检测是否越界了
            if (pageLayout.top >= mViewHeight) {
                // 从Active中移除
                activeItr.remove()
                // 添加到废弃的View中
                addScrapLayout(pageLayout)

                // 只有在如下情况，cur page 才能自动转换为 nextPage
                if (mPageManager.hasPage(PageType.PREVIOUS)) {
                    // 通知换页，说明下一页被删除了，则向前翻页。则上一页就变成当前页，当前页变成了下一页。
                    mPageManager.turnPage(false)
                    hasTurnPage = true
                }
            }
        }

        if (hasTurnPage) {
            // 进行翻页操作
            turnPageLayout(PageType.PREVIOUS)
        }

        // 检测当前页是否存在，如果不存在则添加当前页。
        if (getCurrentPageLayout() == null && mPageManager.hasPage(PageType.CURRENT)) {
            val nextPageLayout = getNextPageLayout()
            val newPageLayout = getScrapLayout()
            newPageLayout.type = PageType.CURRENT

            if (nextPageLayout != null) {
                newPageLayout.scrollY(nextPageLayout.top - mViewHeight)
            }
            // TODO:0 是因为，保证只有 2 个情况这样写的。。。其实想了想不写也行
            mActiveLayouts.add(0, newPageLayout)
        }

        // 区域检测有问题
        // 空白的区域
        val fillArea = when {
            mActiveLayouts.isEmpty() -> {
                // TODO:如果列表不存在可以用的 item，则此次滑动认为无效
                // mViewHeight + scrollY
                mViewHeight
            }
            // TODO:应该创建一个 get() 拿到 top，尽量不要直接调用 mActiveLayouts
            mActiveLayouts.first().top > 0 -> {
                // 如果顶部被删除，那么剩下的只有一个 active Layout。
                // 那么 activeLayout.top 就是顶部的剩余空间了
                mActiveLayouts[0].top
            }
            else -> {
                0
            }
        }

        if (fillArea > 0) {
            // 检测是否存在上一页
            if (mPageManager.hasPage(PageType.PREVIOUS)) {
                // 假设，一个 layout 就能够填充满 spaceArea
                // 如果存在空白区域，则进行填充

                LogHelper.i(TAG, "fillUp: spaceArea")
                val newPageLayout = getScrapLayout()
                newPageLayout.type = PageType.PREVIOUS
                // 滑动到指定位置
                newPageLayout.scrollY(fillArea - mViewHeight)
                // 填充顶部区域，添加到头部
                // 添加到 active 中
                mActiveLayouts.add(0, newPageLayout)

                // 通知换页，说明下一页被删除了，则向前翻页。则上一页就变成当前页，当前页变成了下一页。
                mPageManager.turnPage(false)
                turnPageLayout(PageType.PREVIOUS)
            } else {

                // 如果不存在，需要将
                if (mActiveLayouts.isNotEmpty()) {
                    // 将所有存活的 layout 全部废弃
                    addScrapLayout(mActiveLayouts)
                    // 清空列表
                    mActiveLayouts.clear()
                    // 获取一个能用的 active
                    pageLayout = getScrapLayout()
                    pageLayout.type = PageType.CURRENT
                    // 加入到存活列表中
                    mActiveLayouts.add(pageLayout)
                }
                // 无法继续滑动了，取消动画
                abortAnim()
            }
        }
    }

    private fun turnPageLayout(type: PageType) {
        mActiveLayouts.forEach {
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

    private fun getCurrentPageLayout(): PageLayout? {
        return mActiveLayouts.firstOrNull {
            it.type == PageType.CURRENT
        }
    }

    private fun getNextPageLayout(): PageLayout? {
        return mActiveLayouts.firstOrNull {
            it.type == PageType.NEXT
        }
    }

    private fun addScrapLayout(pageLayout: PageLayout) {
        mScrapLayouts.add(pageLayout)
    }

    private fun addScrapLayout(pageLayouts: ArrayList<PageLayout>) {
        mScrapLayouts.addAll(pageLayouts)
    }

    private fun getScrapLayout(): PageLayout {
        return if (mScrapLayouts.isNotEmpty()) {
            mScrapLayouts.removeFirst().apply {
                // 返回之前，重置状态
                reset()
            }
        } else {
            PageLayout(mViewHeight)
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
        if (mActiveLayouts.isEmpty()) {
            layout()
        }

        canvas.save()
        //裁剪显示区域
        canvas.clipRect(0, 0, mViewWidth, mViewHeight)

        // TODO：有没有更好看的方案，要 save 两层太蛋疼了
        mActiveLayouts.forEach { pageLayout ->
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
    private class PageLayout(val height: Int) {
        // Page Top 距离 ViewPort Top 的位置
        var top = 0
            private set

        // Page Bottom 距离 ViewPort Top 的位置
        var bottom = height
            private set

        // 当前布局针对的页面类型
        var type: PageType? = null

        /**
         * 竖直滑动距离
         */
        fun scrollY(y: Int) {
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

