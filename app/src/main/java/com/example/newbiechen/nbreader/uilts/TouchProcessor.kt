package com.example.newbiechen.nbreader.uilts

import android.content.Context
import android.os.Handler
import android.view.MotionEvent
import android.view.ViewConfiguration
import kotlin.math.abs

/**
 *  author : newbiechen
 *  date : 2019-08-30 17:50
 *  description : 点击事件处理器
 */

class TouchProcessor(context: Context, private var fingerTouch: OnTouchListener) {
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

    private var mHandler: Handler = Handler()

    private var mContext = context.applicationContext

    fun onTouchEvent(event: MotionEvent) {
        val x = event.x.toInt()
        val y = event.y.toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 是否是延迟单击事件
                if (isPendingSingleTap) {
                    // 取消延迟单击事件
                    mHandler.removeCallbacks(singleTapRunnable)
                    isPendingSingleTap = false
                    isPendingPress = false

                    // 升级为延迟双击事件
                    isPendingDoubleTap = true
                } else {
                    // 则设置延迟长按事件
                    mHandler.postDelayed(longPressRunnable, 2 * ViewConfiguration.getLongPressTimeout().toLong())
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
                val minSlop = ViewConfiguration.get(mContext).scaledTouchSlop
                val isMove = abs(mPressedX - x) > minSlop || abs(mPressedY - y) > mPressedY
                // 如果移动，直接取消双击事件
                if (isMove) {
                    isPendingDoubleTap = false
                }

                // 如果在移动之前触发过长按事件
                if (isPerformLongPress) {
                    fingerTouch.onMoveAfterLongPress(x, y)
                } else {
                    // 是否触发延迟按下事件
                    if (isPendingPress) {
                        // 如果移动了最小距离
                        if (isMove) {
                            // 是否触发了单击事件
                            if (isPendingSingleTap) {
                                // 取消单击事件
                                mHandler.removeCallbacks(singleTapRunnable)
                                isPendingSingleTap = false
                            }

                            // 是否触发了长按事件
                            if (isPendingLongPress) {
                                // 取消长按事件
                                mHandler.removeCallbacks(longPressRunnable)
                                isPendingLongPress = false
                            }

                            // 触发按下事件
                            fingerTouch.onPress(mPressedX, mPressedY)
                            // 取消延迟按下
                            isPendingPress = false
                        }
                    }

                    // 如果已经处理按下事件，则处理移动事件
                    if (!isPendingPress) {
                        fingerTouch.onMove(x, y)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                // 是否是延迟双击事件
                if (isPendingDoubleTap) {
                    fingerTouch.onDoubleTap(x, y)
                } else if (isPerformLongPress) { // 是否已经执行长按事件
                    fingerTouch.onReleaseAfterLongPress(x, y)
                } else {
                    // 如果长按事件未执行，则取消
                    if (isPendingLongPress) {
                        mHandler.removeCallbacks(longPressRunnable)
                        isPendingLongPress = false
                    }

                    // 是否还存在延迟按下
                    if (isPendingPress) {
                        // 是否支持双击
                        if (isEnableDoubleTap) {
                            mHandler.postDelayed(singleTapRunnable, ViewConfiguration.getDoubleTapTimeout().toLong())
                            isPendingSingleTap = true
                        } else {
                            fingerTouch.onSingleTap(x, y)
                        }
                    } else {
                        fingerTouch.onRelease(x, y)
                    }
                }

            }
            MotionEvent.ACTION_CANCEL -> {
                isPendingPress = false
                isPendingDoubleTap = false
                isPendingSingleTap = false
                isPendingLongPress = false
                isPerformLongPress = false

                mHandler.removeCallbacks(singleTapRunnable)
                mHandler.removeCallbacks(longPressRunnable)
                // 发送取消事件
                fingerTouch.onCancelTap()
            }
        }
    }

    /**
     * 单击事件
     */
    private var singleTapRunnable = Runnable {
        fingerTouch.onSingleTap(mPressedX, mPressedY)
        isPendingPress = false
    }

    /**
     * 长按事件
     */
    private var longPressRunnable = Runnable {
        fingerTouch.onLongPress(mPressedX, mPressedY)
        isPerformLongPress = true
    }

    interface OnTouchListener {
        /**
         * 手指按下事件
         */
        fun onPress(x: Int, y: Int)

        /**
         * 手指移动事件
         */
        fun onMove(x: Int, y: Int)

        /**
         * 手指释放事件：表示在 DOWN 过程中调用了 MOVE 时，UP 会调用该方法。
         */
        fun onRelease(x: Int, y: Int)

        /**
         * 手指长按事件
         */
        fun onLongPress(x: Int, y: Int)

        /**
         * 手指长按后移动事件
         */
        fun onMoveAfterLongPress(x: Int, y: Int)

        /**
         * 手指长按后释放事件
         */
        fun onReleaseAfterLongPress(x: Int, y: Int)

        /**
         * 手指一次点击事件：表示只执行了 DOWN + UP 会调用该方法
         */
        fun onSingleTap(x: Int, y: Int)

        /**
         * 手指双击事件
         */
        fun onDoubleTap(x: Int, y: Int)

        /**
         * 手指取消事件
         */
        fun onCancelTap()
    }
}