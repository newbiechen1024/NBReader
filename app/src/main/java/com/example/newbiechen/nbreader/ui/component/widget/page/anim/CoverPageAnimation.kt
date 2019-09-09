package com.example.newbiechen.nbreader.ui.component.widget.page.anim

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.view.View
import com.example.newbiechen.nbreader.ui.component.widget.page.PageManager
import com.example.newbiechen.nbreader.uilts.LogHelper
import kotlin.math.abs

/**
 *  author : newbiechen
 *  date : 2019-09-05 16:41
 *  description :
 */

class CoverPageAnimation(view: View, pageManager: PageManager) : PageAnimation(view, pageManager) {

    private val mBackShadowDrawableLR: GradientDrawable
    private val mSpaceRect: Rect = Rect()
    private val mBitmapRect: Rect = Rect()

    companion object {
        private const val TAG = "CoverPageAnimation"
    }

    init {
        val mBackShadowColors = intArrayOf(0x66000000, 0x00000000)
        mBackShadowDrawableLR = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors
        )
        mBackShadowDrawableLR.gradientType = GradientDrawable.LINEAR_GRADIENT
    }

    override fun setup(w: Int, h: Int) {
        super.setup(w, h)
        mSpaceRect.set(0, 0, w, h)
        mBitmapRect.set(0, 0, w, h)
    }

    override fun drawStatic(canvas: Canvas) {
        canvas.drawBitmap(getFromPage(), 0f, 0f, null)
    }

    override fun drawMove(canvas: Canvas) {
        when (mDirection) {
            Direction.PREVIOUS -> {
                mSpaceRect.left = mViewWidth - mTouchX
                mBitmapRect.right = mTouchX
                canvas.drawBitmap(getFromPage(), 0f, 0f, null)
                canvas.drawBitmap(getToPage(), mSpaceRect, mBitmapRect, null)
                addShadow(mTouchX, canvas)
            }

            Direction.NEXT -> {
                var dis = mViewWidth - mStartX + mTouchX
                if (dis > mViewWidth) {
                    dis = mViewWidth
                }
                //计算bitmap截取的区域
                mSpaceRect.left = mViewWidth - dis
                //计算bitmap在canvas显示的区域
                mBitmapRect.right = dis
                canvas.drawBitmap(getToPage(), 0f, 0f, null)
                canvas.drawBitmap(getFromPage(), mSpaceRect, mBitmapRect, null)
                addShadow(dis, canvas)
            }
        }
    }

    //添加阴影
    private fun addShadow(left: Int, canvas: Canvas) {
        mBackShadowDrawableLR.setBounds(left, 0, left + 30, mViewHeight)
        mBackShadowDrawableLR.draw(canvas)
    }

    override fun startAnimInternal() {
        var dx = 0
        when (mDirection) {
            Direction.PREVIOUS -> dx = if (mStatus == Status.AutoBackward) {
                -mTouchX
            } else {
                mViewWidth - mTouchX
            }
            Direction.NEXT -> dx = if (mStatus == Status.AutoBackward) {
                var dis = mViewWidth - mStartX + mTouchX
                if (dis > mViewWidth) {
                    dis = mViewWidth
                }
                mViewWidth - dis
            } else {
                -(mTouchX + (mViewWidth - mStartX))
            }
        }

        //滑动速度保持一致
        val duration = 400 * abs(dx) / mViewWidth
        mScroller.startScroll(mTouchX, 0, dx, 0, duration)
    }
}