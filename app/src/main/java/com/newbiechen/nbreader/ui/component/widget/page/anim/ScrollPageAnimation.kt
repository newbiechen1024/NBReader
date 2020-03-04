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
        // TODO:可以外置，设置灵敏度？
        private const val VELOCITY_DURATION = 1500
    }

    var isRunning = false
        private set

    private val mView = view
    private val mPageManager = pageManager

    // 动画滑动器
    private val mScroller = Scroller(view.context, LinearInterpolator())

    // 速度检测器
    private var mVelocity: VelocityTracker? = null

    // 元素列表 (直接使用 curLayout 和 nextLayout，然后有个 ActiveLayout)
    private val mActiveLayoutList: ArrayList<PageLayout> = ArrayList(2)
    private val mScrapLayoutDeque: ArrayDeque<PageLayout> = ArrayDeque(2)

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

        // 进行重新布局
        layout()
    }
    // TODO:这个改成 press、move、release

    /**
     *  点击事件
     */
    fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        // 初始化速度追踪器
        if (mVelocity == null) {
            mVelocity = VelocityTracker.obtain()
        }

        // TODO:加入点击状态，PRESS、MOVE、FLING
        mVelocity!!.addMovement(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isRunning = false
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
                    isRunning = true
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

    private fun setStartPoint(x: Int, y: Int) {
        // 设置起始点
        mStartX = x
        mStartY = y
        // 设置触碰点
        mTouchX = x
        mTouchY = y
    }

    private fun setTouchPoint(x: Int, y: Int) {
        mLastX = mTouchX
        mLastY = mTouchY

        mTouchX = x
        mTouchY = y
    }

    // 获取竖直滑动的距离
    private fun getScrollY(): Int {
        return mTouchY - mLastY
    }

    /**
     * 进行布局操作
     */
    private fun layout() {
        // TODO：如何知道是滑动状态,可能存在之前的 touch，没有被销毁的情况。
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
        val activeItr = mActiveLayoutList.iterator()
        var pageLayout: PageLayout

        // 为每个 layout 加上滑动距离，并检测存在越界的情况 (bottom <= 0)，如果越界就移除该 item
        // 如果顶部被删除，则需要 turnPage() Next，翻页。
        while (activeItr.hasNext()) {
            pageLayout = activeItr.next()
            // 滑动 layout
            pageLayout.scrollY(scrollY)

            // 如果 scrollY >= viewHeight 整个逻辑就会出问题。(默认这种情况会不发生)

            // 检测是否越界了
            if (pageLayout.bottom <= 0) {
                // 从Active中移除
                activeItr.remove()
                // 添加到废弃的View中
                mScrapLayoutDeque.add(pageLayout)
                // 通知换页，说明上一页没了，则下一页就是当前页了
                mPageManager.turnPage(true)

/*                LogHelper.i(TAG, "fillDown: turnPage")*/

            }
        }

        // 空白的区域
        val spaceArea = if (mActiveLayoutList.isEmpty()) {
            // TODO:如果列表不存在可以用的 item，则此次滑动认为无效
            // mViewHeight + scrollY
            mViewHeight
        } else {
            if (mActiveLayoutList.last().bottom < mViewHeight) {
                // 如果顶部被删除，那么剩下的只有一个 active Layout。
                // 那么 viewHeight - bottom 就是剩余空间
                mViewHeight - mActiveLayoutList[0].bottom
            } else {
                0
            }
        }

        // 检测是否存在下一页
        if (mPageManager.hasPage(PageType.NEXT)) {
            // 假设，一个 layout 就能够填充满 spaceArea
            // 如果存在空白区域，则进行填充
            if (spaceArea > 0) {
                val newPageLayout = getScrapLayout()
                // 滑动到指定位置
                newPageLayout.scrollY(mViewHeight - spaceArea)
                // 添加到 active 中
                mActiveLayoutList.add(newPageLayout)

/*                LogHelper.i(TAG, "fillDown: spaceArea")*/


            }
        } else {
            if (mActiveLayoutList.isNotEmpty()) {

                if (mActiveLayoutList.last().bottom < mViewHeight) {
                    // 将所有存活的 layout 全部废弃
                    addScrapLayout(mActiveLayoutList)
                    // 清空列表
                    mActiveLayoutList.clear()
                    // 获取一个能用的 active
                    pageLayout = getScrapLayout()
                    // 加入到存活列表中
                    mActiveLayoutList.add(pageLayout)
                    // 无法继续滑动了，取消动画
                    abortAnim()

/*                    LogHelper.i(TAG, "fillDown: abortAnim")*/

                }
            } else {
                // 无法继续滑动了，取消动画
                abortAnim()
            }
        }

        mActiveLayoutList.forEach {
            LogHelper.i(TAG, "fillDown: item = $it")
        }
    }

    /**
     * 向下滑动，填充顶部空白区域
     */
    private fun fillUp(scrollY: Int) {
        LogHelper.i(TAG, "fillUp: $scrollY")

        val activeItr = mActiveLayoutList.iterator()
        var pageLayout: PageLayout

        // 为每个 layout 加上滑动距离，并检测存在越界的情况 (top >= viewHeight)，如果越界就移除该 item
        // 如果顶部被删除，则需要 turnPage() Next，翻页。
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

                // TODO:如果这里判断 pageLayout.top > mViewHeight 成功，当前页没转换。那么之后如果新加入顶部，那么这时候就需要 turnPage 了。
                // 只有在如下情况，cur page 才能自动转换为 nextPage
                if (mPageManager.hasPage(PageType.PREVIOUS) && pageLayout.top > mViewHeight) {
                    // 通知换页，说明下一页被删除了，则向前翻页。则上一页就变成当前页，当前页变成了下一页。
                    mPageManager.turnPage(false)

                    LogHelper.i(TAG, "fillUp: turnPage")
                }
            }
        }

        // TODO：还有一个 current 状态，对于 next 来说无所谓，但是对于 prev 来说就是个问题。(就是这个问题)

        // 空白的区域
        val spaceArea = if (mActiveLayoutList.isEmpty()) {
            // TODO:如果列表不存在可以用的 item，则此次滑动认为无效
            // mViewHeight + scrollY
            mViewHeight
        } else {
            if (mActiveLayoutList.first().top > 0) {
                // 如果顶部被删除，那么剩下的只有一个 active Layout。
                // 那么 activeLayout.top 就是顶部的剩余空间了
                mActiveLayoutList[0].top
            } else {
                0
            }
        }

        // hasPage() 检测失败，此时在第二个页面。

        // 检测是否存在上一页
        if (mPageManager.hasPage(PageType.PREVIOUS)) {
            // 假设，一个 layout 就能够填充满 spaceArea
            // 如果存在空白区域，则进行填充
            if (spaceArea > 0) {
                LogHelper.i(TAG, "fillUp: spaceArea")
                val newPageLayout = getScrapLayout()
                // 滑动到指定位置
                newPageLayout.scrollY(spaceArea - mViewHeight)
                // 填充顶部区域，添加到头部
                // 添加到 active 中
                mActiveLayoutList.add(0, newPageLayout)

                LogHelper.i(TAG, "fillUp: spaceArea $newPageLayout")
            }
        } else {
            if (mActiveLayoutList.isNotEmpty()) {
                if (mActiveLayoutList.first().top > 0) {
                    // 将所有存活的 layout 全部废弃
                    addScrapLayout(mActiveLayoutList)
                    // 清空列表
                    mActiveLayoutList.clear()
                    // 获取一个能用的 active
                    pageLayout = getScrapLayout()
                    // 加入到存活列表中
                    mActiveLayoutList.add(pageLayout)
                    // 无法继续滑动了，取消动画
                    abortAnim()

                    LogHelper.i(TAG, "fillUp: abortAnim")

                }
            } else {
                // 无法继续滑动了，取消动画
                abortAnim()
            }
        }

        mActiveLayoutList.forEach {
            LogHelper.i(TAG, "fillUp: item = $it")
        }
    }

    private fun addScrapLayout(pageLayout: PageLayout) {
        mScrapLayoutDeque.add(pageLayout)
    }

    private fun addScrapLayout(pageLayouts: ArrayList<PageLayout>) {
        mScrapLayoutDeque.addAll(pageLayouts)
    }

    private fun getScrapLayout(): PageLayout {
        return if (mScrapLayoutDeque.isNotEmpty()) {
            mScrapLayoutDeque.removeFirst().apply {
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
        // 如果数据为空，则会发起布局请求
        if (mActiveLayoutList.isEmpty()) {
            layout()
        }

        canvas.save()
        //裁剪显示区域
        canvas.clipRect(0, 0, mViewWidth, mViewHeight)

        // TODO：有没有更好看的方案
        mActiveLayoutList.forEachIndexed { index, pageLayout ->
            canvas.save()
            canvas.translate(0f, pageLayout.top.toFloat())
            if (index == 0) {
                canvas.drawPicture(mPageManager.getPage(PageType.CURRENT))
            } else if (index == 1) {
                // 位移操作
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
        isRunning = true

        mScroller.fling(
            0, mTouchY, 0, mVelocity!!.yVelocity.toInt()
            , 0, 0, Int.MAX_VALUE * -1, Int.MAX_VALUE
        )
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
        // 重置一切信息
        isRunning = false
/*        mStartX = 0
        mStartY = 0
        mTouchX = 0
        mTouchY = 0
        mLastX = 0
        mLastY = 0*/

        // 进行刷新
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

