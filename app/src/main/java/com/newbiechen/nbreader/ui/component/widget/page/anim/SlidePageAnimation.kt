package com.newbiechen.nbreader.ui.component.widget.page.anim

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import kotlin.math.abs

/**
 *  author : newbiechen
 *  date : 2019-09-05 16:21
 *  description :
 */

class SlidePageAnimation(view: View, pageManager: IPageAnimCallback) :
    PageAnimation(view, pageManager) {
    // 图片在屏幕的可展示区域
    private var mFromSpaceRect = Rect()
    // 选取图片的片段区域
    private var mFromBitmapRect = Rect()

    private var mToSpaceRect = Rect()

    private var mToBitmapRect = Rect()

    override fun setViewPort(w: Int, h: Int) {
        super.setViewPort(w, h)
        // 设置宽高
        mFromSpaceRect.set(0, 0, w, h)
        mFromBitmapRect.set(0, 0, w, h)
        mToSpaceRect.set(0, 0, w, h)
        mToBitmapRect.set(0, 0, w, h)
    }

    override fun drawStatic(canvas: Canvas) {
        canvas.drawBitmap(getFromPage(), 0f, 0f, null)
    }

    override fun drawMove(canvas: Canvas) {
        var dis: Int
        when (mDirection) {
            Direction.PREVIOUS -> {
                dis = mTouchX - mStartX
                if (dis < 0) {
                    dis = 0
                    mStartX = mTouchX
                }

                mFromSpaceRect.left = mViewWidth - dis
                mFromBitmapRect.right = dis

                mToSpaceRect.right = mViewWidth - dis
                mToBitmapRect.left = dis

                canvas.drawBitmap(getFromPage(), mToSpaceRect, mToBitmapRect, null)
                canvas.drawBitmap(getToPage(), mFromSpaceRect, mFromBitmapRect, null)
            }
            Direction.NEXT -> {
                //左半边的剩余区域
                dis = mViewWidth - mStartX + mTouchX
                if (dis > mViewWidth) {
                    dis = mViewWidth
                }
                //计算bitmap截取的区域
                mFromSpaceRect.left = mViewWidth - dis
                //计算bitmap在canvas显示的区域
                mFromBitmapRect.right = dis
                //计算下一页截取的区域
                mToSpaceRect.right = mViewWidth - dis
                //计算下一页在canvas显示的区域
                mToBitmapRect.left = dis

                canvas.drawBitmap(getFromPage(), mFromSpaceRect, mFromBitmapRect, null)
                canvas.drawBitmap(getToPage(), mToSpaceRect, mToBitmapRect, null)
            }
            else -> {
                // 不处理
            }
        }
    }

    override fun startAnim() {
        var dx = 0
        when (mDirection) {
            Direction.PREVIOUS -> dx = if (mStatus == Status.AutoBackward) {
                -abs(mTouchX - mStartX)
            } else {
                mViewWidth - (mTouchX - mStartX)
            }
            Direction.NEXT -> dx = if (mStatus == Status.AutoBackward) {
                var distance = (mViewWidth - mStartX + mTouchX)
                if (distance > mViewWidth) {
                    distance = mViewWidth
                }
                mViewWidth - distance
            } else {
                -(mTouchX + (mViewWidth - mStartX))
            }
        }

        //滑动速度保持一致
        val duration = 400 * abs(dx) / mViewWidth
        mScroller.startScroll(mTouchX, 0, dx, 0, duration)
    }
}