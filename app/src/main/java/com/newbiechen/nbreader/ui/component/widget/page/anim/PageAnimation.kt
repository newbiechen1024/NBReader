package com.newbiechen.nbreader.ui.component.widget.page.anim

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Scroller
import com.newbiechen.nbreader.ui.component.widget.page.PageType
import kotlin.math.abs

/**
 *  author : newbiechen
 *  date : 2019-08-31 16:37
 *  description :页面动画
 */

abstract class PageAnimation(view: View, pageManager: IPageAnimCallback) {
    companion object {
        private const val TAG = "PageAnimation"
        private const val MIN_SCROLL_SLOP = 5
    }

    // 指定动画的视图
    protected val mView = view
    // 页面管理器
    // 动画滑动器
    protected val mScroller = Scroller(view.context, LinearInterpolator())
    // 动画方向
    protected open var mDirection = Direction.NONE
    // 动画状态
    protected var mStatus = Status.NONE
    //起始点
    protected var mStartX: Int = 0
    protected var mStartY: Int = 0
    //触碰点
    protected open var mTouchX: Int = 0
    protected open var mTouchY: Int = 0

    //上一个触碰点
    protected var mLastX: Int = 0
    protected var mLastY: Int = 0

    // 视图的宽高
    protected var mViewWidth = 0
    protected var mViewHeight = 0

    // 是否正在执行翻页(保证，有方向并且有状态才算翻页)
    val isRunning: Boolean
        get() = mStatus != Status.NONE && mDirection != Direction.NONE

    private var mAnimListener: IPageAnimListener? = null

    private val mPageManager = pageManager

    // 是否取消翻页
    private var isCancel = false

    protected abstract fun drawStatic(canvas: Canvas)

    protected abstract fun drawMove(canvas: Canvas)

    fun setAnimationListener(pageAnimListener: IPageAnimListener) {
        mAnimListener = pageAnimListener
    }

    // 设置宽高 ==> 因为宽高可变的
    open fun setup(w: Int, h: Int) {
        if (w == 0 || h == 0
            || mViewWidth == w || mViewHeight == h
        ) {
            return
        }

        mViewWidth = w
        mViewHeight = h

        // 通知动画页面改变
        mPageManager.onPageSizeChanged(w, h)

        abortAnim()
    }

    // 触碰页面
    fun pressPage(x: Int, y: Int) {
        // 如果当前正在执行动画，先取消动画
        when (mStatus) {
            Status.AutoForward, Status.AutoBackward -> abortAnim()
            else -> {
                // 不处理
            }
        }

        setStartPoint(x, y)

        // 设置为按下状态
        mStatus = Status.ManualPress
        // 重置状态
        isCancel = false
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
    fun movePage(x: Int, y: Int) {
        when (mStatus) {
            Status.NONE, Status.AutoForward, Status.AutoBackward -> pressPage(x, y)
        }

        // 上一触碰点
        setTouchPoint(x, y)

        // 上一个状态是否是按下状态
        var lastStatusIsPress = false

        // 是否是第一次滑动，决定翻页的方向
        if (mStatus == Status.ManualPress) {
            lastStatusIsPress = true

            // 如果是翻阅到上一页
            mDirection = if (x - mStartX > 0) {
                if (mPageManager.hasPage(PageType.PREVIOUS)) Direction.PREVIOUS else Direction.NONE
            } else if (x - mStartX < 0) {
                // 翻阅到下一页
                if (mPageManager.hasPage(PageType.NEXT)) Direction.NEXT else Direction.NONE
            } else { // 绘制结果
                // 根据当前点击位置决定移动方向
                if (x < mViewWidth / 2) {
                    if (mPageManager.hasPage(PageType.PREVIOUS)) Direction.PREVIOUS else Direction.NONE
                } else {
                    if (mPageManager.hasPage(PageType.NEXT)) Direction.NEXT else Direction.NONE
                }
            }
            // 请求翻页
        } else {

            // TODO:翻页灵敏度有问题 ==> 需要之后改
            if (abs(x - mLastX) > MIN_SCROLL_SLOP) {
                // 如果不是第一次滑动，判断是否取消滑动
                when (mDirection) {
                    Direction.PREVIOUS -> {
                        isCancel = x - mLastX < 0
                    }
                    Direction.NEXT -> {
                        isCancel = x - mLastX > 0
                    }
                }
            }
        }

        // 设置状态
        mStatus = Status.ManualMove

        // 如果没有方向则不进行绘制
        if (mDirection != Direction.NONE) {
            // 上一个是否是按下状态
            if (lastStatusIsPress) {
                // 通知翻页开始
                mAnimListener?.onAnimationStart()
            }

            // 请求刷新
            mView.postInvalidate()
        }
    }

    private fun setTouchPoint(x: Int, y: Int) {
        mLastX = mTouchX
        mLastY = mTouchY

        mTouchX = x
        mTouchY = y
    }


    // 释放页面
    fun releasePage(x: Int, y: Int) {
        when (mStatus) {
            Status.NONE, Status.AutoForward, Status.AutoBackward -> pressPage(x, y)
        }

        setTouchPoint(x, y)

        // 如果是按下事件，则还需要计算滑动方向
        if (mStatus == Status.ManualPress) {
            // 根据当前点击位置决定移动方向
            mDirection = if (x < mViewWidth / 2) {
                if (mPageManager.hasPage(PageType.PREVIOUS)) Direction.PREVIOUS else Direction.NONE
            } else {
                if (mPageManager.hasPage(PageType.NEXT)) Direction.NEXT else Direction.NONE
            }
        }

        when (mDirection) {
            Direction.NONE -> {
                // 表示没有下一页
                mStatus = Status.NONE
            }
            else -> {
                mStatus = if (isCancel) Status.AutoBackward else Status.AutoForward
                // 启动动画
                startAnimInternal()
                mView.postInvalidate()
            }
        }

        // 重置
        isCancel = false
    }

    fun draw(canvas: Canvas) {
        // 如果正在运行，
        if (isRunning) {
            drawMove(canvas)
        } else {
            drawStatic(canvas)
        }
    }

    protected fun getFromPage() = mPageManager.getPage(PageType.CURRENT)

    protected fun getToPage(): Bitmap {
        return when (mDirection) {
            Direction.PREVIOUS -> mPageManager.getPage(PageType.PREVIOUS)
            Direction.NEXT -> mPageManager.getPage(PageType.NEXT)
            else -> mPageManager.getPage(PageType.CURRENT)
        }
    }

    /**
     * 启动翻页动画
     * 注：启动翻页动画前必须重写 [android.view.View.computeScroll] 调用 [computeScroll]
     */
    fun startAnim(x: Int, y: Int) {
        mStatus = Status.NONE
        // 取巧了，releasePage() 附带有启动动画的实现
        releasePage(x, y)
    }

    // 实际翻页处理
    abstract fun startAnimInternal()

    // 取消动画
    fun abortAnim() {
        // 如果正在执行动画
        if (!mScroller.isFinished) {
            // 取消执行
            mScroller.abortAnimation()
            // 直接滑动到最终点
            setTouchPoint(mScroller.finalX, mScroller.finalY)
            // 请求刷新
            mView.postInvalidate()
            // 通知动画完成
            finishAnim()
        }

        mStatus = Status.NONE
        mDirection = Direction.NONE
    }

    fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            // 模拟移动点击
            setTouchPoint(mScroller.currX, mScroller.currY)

            mView.postInvalidate()

            // 如果滑动结束
            if (mScroller.isFinished) {
                // 通知动画完成
                finishAnim()
            }
        }
    }

    /**
     * 通知动画完成
     */
    protected fun finishAnim() {
        // 如果是恢复原样，则不交换页面
        if (mStatus == Status.AutoForward) {
            when (mDirection) {
                Direction.PREVIOUS -> mPageManager.turnPage(false)
                Direction.NEXT -> mPageManager.turnPage(true)
            }
        }

        // 通知翻页结束
        mAnimListener?.onAnimationEnd()

        // 重置状态
        mStatus = Status.NONE
        mDirection = Direction.NONE
    }

    // 页面动画方向
    protected enum class Direction {
        NONE,
        PREVIOUS, // 上一页
        NEXT, // 下一页
    }

    // 动画状态
    protected enum class Status {
        NONE, // 无状态
        ManualPress, // 手动按下
        ManualMove, // 手动移动
        AutoForward, // 自动向前翻页
        AutoBackward // 自动取消翻页
    }
}