package com.newbiechen.nbreader.ui.component.widget.page.action

import android.content.Context
import android.os.Handler
import android.view.MotionEvent
import android.view.ViewConfiguration
import com.newbiechen.nbreader.uilts.LogHelper
import kotlin.math.abs

/**
 *  author : newbiechen
 *  date : 2019-08-30 17:50
 *  description : 文本手势探测
 *
 */

class TextGestureDetector(
    context: Context,
    private var textGestureListener: OnTextGestureListener
) {

    companion object {
        private const val TAG = "TextGestureDetector"
    }

    // 是否允许双击
    var isEnableDoubleTap = false

    // 延迟按下标记
    private var isPendingPress = false

    // 延迟双击标记
    private var isPendingDoubleTap = false

    // 是否延迟长按
    private var isPendingLongPress = false

    // 延迟单击标记
    private var isPendingSingleTap = false

    // 是否触发长按事件
    private var isPerformLongPress = false

    private var mPressedX = 0
    private var mPressedY = 0

    private var isMove = false

    private var mHandler: Handler = Handler()

    private var mPressEvent: MotionEvent? = null

    private val MIN_SLOP = ViewConfiguration.get(context).scaledTouchSlop

    fun onTouchEvent(event: MotionEvent) {
        val x = event.x.toInt()
        val y = event.y.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mPressEvent = event
                isMove = false
                // 是否是延迟单击事件
                if (isPendingSingleTap) {
                    // 取消延迟单击事件
                    removeMotionRunnable(singleTapRunnable)
                    isPendingSingleTap = false
                    isPendingPress = false

                    // 升级为延迟双击事件
                    isPendingDoubleTap = true
                } else {
                    // 则设置延迟长按事件
                    postMotionRunnable(
                        longPressRunnable,
                        mPressEvent!!,
                        2 * ViewConfiguration.getLongPressTimeout().toLong()
                    )

                    isPendingLongPress = true
                    // 添加延时按下标记
                    isPendingPress = true
                    // 重置长按标记
                    isPerformLongPress = false
                }

                mPressedX = x
                mPressedY = y
            }
            MotionEvent.ACTION_MOVE -> {
                // 最小滑动距离
                if (!isMove) {
                    isMove = abs(mPressedX - x) > MIN_SLOP || abs(mPressedY - y) > MIN_SLOP
                }

                // 如果移动，直接取消双击事件
                if (isMove) {
                    isPendingDoubleTap = false
                }

                // 如果在移动之前触发过长按事件
                if (isPerformLongPress) {
                    textGestureListener.onMoveAfterLongPress(event)
                } else {
                    // 是否触发延迟按下事件
                    if (isPendingPress) {
                        // 如果移动了最小距离
                        if (isMove) {
                            // 是否触发了单击事件
                            if (isPendingSingleTap) {
                                // 取消单击事件
                                removeMotionRunnable(singleTapRunnable)
                                isPendingSingleTap = false
                            }

                            // 是否触发了长按事件
                            if (isPendingLongPress) {
                                // 取消长按事件
                                removeMotionRunnable(longPressRunnable)
                                isPendingLongPress = false
                            }

                            // 触发按下事件
                            textGestureListener.onPress(mPressEvent!!)
                            // 取消延迟按下
                            isPendingPress = false
                        }
                    }

                    // 如果已经处理按下事件，则处理移动事件
                    if (!isPendingPress) {
                        textGestureListener.onMove(event)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                // 是否是延迟双击事件
                if (isPendingDoubleTap) {
                    textGestureListener.onDoubleTap(event)
                } else if (isPerformLongPress) { // 是否已经执行长按事件
                    textGestureListener.onReleaseAfterLongPress(event)
                } else {
                    // 如果长按事件未执行，则取消
                    if (isPendingLongPress) {
                        removeMotionRunnable(longPressRunnable)
                        isPendingLongPress = false
                    }

                    // 是否还存在延迟按下
                    if (isPendingPress) {
                        // 是否支持双击
                        if (isEnableDoubleTap) {
                            postMotionRunnable(
                                singleTapRunnable,
                                mPressEvent!!,
                                ViewConfiguration.getDoubleTapTimeout().toLong()
                            )
                            isPendingSingleTap = true
                        } else {
                            textGestureListener.onSingleTap(mPressEvent!!)
                        }
                    } else {
                        textGestureListener.onRelease(event)
                    }
                }

                mPressEvent = null
            }
            MotionEvent.ACTION_CANCEL -> {
                isPendingPress = false
                isPendingDoubleTap = false
                isPendingSingleTap = false
                isPendingLongPress = false
                isPerformLongPress = false

                removeMotionRunnable(singleTapRunnable)
                removeMotionRunnable(longPressRunnable)
                // 发送取消事件
                textGestureListener.onCancelTap(event)
            }
        }
    }

    private fun postMotionRunnable(
        runnable: MotionRunnable,
        event: MotionEvent,
        delayMillis: Long
    ) {
        // 初始化
        runnable.init(event)
        // 投递
        mHandler.postDelayed(
            runnable,
            delayMillis
        )
    }

    private fun removeMotionRunnable(runnable: MotionRunnable) {
        mHandler.removeCallbacks(runnable)
        runnable.release()
    }

    /**
     * 单击事件
     */
    private var singleTapRunnable = object : MotionRunnable() {
        override fun runEvent(event: MotionEvent) {
            textGestureListener.onSingleTap(event)
            isPendingPress = false
        }
    }

    /**
     * 长按事件
     */
    private var longPressRunnable = object : MotionRunnable() {
        override fun runEvent(event: MotionEvent) {
            textGestureListener.onSingleTap(event)
            isPendingPress = false
        }
    }

    private abstract inner class MotionRunnable : Runnable {
        private var pressEvent: MotionEvent? = null

        fun init(event: MotionEvent) {
            if (pressEvent != null) {
                pressEvent!!.recycle()
            }
            // 将 event 进行复制
            pressEvent = MotionEvent.obtain(event)
        }

        fun release() {
            // 释放 event
            pressEvent?.recycle()
            pressEvent = null
        }

        override fun run() {
            if (pressEvent == null) {
                return
            }

            runEvent(pressEvent!!)
            release()
        }

        abstract fun runEvent(event: MotionEvent)
    }

    interface OnTextGestureListener {
        /**
         * 手指按下事件
         */
        fun onPress(event: MotionEvent)

        /**
         * 手指移动事件
         */
        fun onMove(event: MotionEvent)

        /**
         * 手指释放事件：表示在 DOWN 过程中调用了 MOVE 时，UP 会调用该方法。
         */
        fun onRelease(event: MotionEvent)

        /**
         * 手指长按事件
         */
        fun onLongPress(event: MotionEvent)

        /**
         * 手指长按后移动事件
         */
        fun onMoveAfterLongPress(event: MotionEvent)

        /**
         * 手指长按后释放事件
         */
        fun onReleaseAfterLongPress(event: MotionEvent)

        /**
         * 手指一次点击事件：表示只执行了 DOWN + UP 会调用该方法
         */
        fun onSingleTap(event: MotionEvent)

        /**
         * 手指双击事件
         */
        fun onDoubleTap(event: MotionEvent)

        /**
         * 手指取消事件
         */
        fun onCancelTap(event: MotionEvent)
    }
}