package com.newbiechen.nbreader.ui.component.widget.page.action

import android.view.MotionEvent
import com.newbiechen.nbreader.ui.component.widget.page.text.TextPageView

/**
 *  author : newbiechen
 *  date : 2019-08-30 16:20
 *  description :将点击事件转换成页面的行为事件
 */

typealias TextActionListener = (action: PageAction) -> Unit

class TextActionProcessor(private val textPageView: TextPageView) {

    companion object {
        private const val TAG = "PageActionProcessor"
    }

    private var mTouchEventProcessor =
        TextGestureDetector(textPageView.context, OnGestureCallback())

    private var mActionListener: TextActionListener? = null

    fun setPageActionListener(actionListener: TextActionListener) {
        mActionListener = actionListener
    }

    fun onTouchEvent(event: MotionEvent) {
        mTouchEventProcessor.onTouchEvent(event)
    }

    private fun dispatchAction(action: PageAction) {
        mActionListener?.invoke(action)
    }

    private inner class OnGestureCallback : TextGestureDetector.OnTextGestureListener {
        // TODO:需要创建一个 RecyclerBin 解决 MotionAction 被频繁创建的问题？(之后优化)
        override fun onPress(event: MotionEvent) {
            dispatchAction(
                MotionAction(MotionType.PRESS, event)
            )
        }

        override fun onMove(event: MotionEvent) {
            dispatchAction(
                MotionAction(MotionType.MOVE, event)
            )
        }

        override fun onRelease(event: MotionEvent) {
            dispatchAction(
                MotionAction(MotionType.RELEASE, event)
            )
        }

        override fun onLongPress(event: MotionEvent) {
            dispatchAction(
                MotionAction(MotionType.LONG_PRESS, event)
            )
        }

        override fun onMoveAfterLongPress(event: MotionEvent) {
        }

        override fun onReleaseAfterLongPress(event: MotionEvent) {
        }

        override fun onSingleTap(event: MotionEvent) {
            dispatchAction(
                MotionAction(MotionType.SINGLE_TAP, event)
            )
        }

        override fun onDoubleTap(event: MotionEvent) {
            dispatchAction(
                MotionAction(MotionType.DOUBLE_TAP, event)
            )
        }

        override fun onCancelTap(event: MotionEvent) {
            dispatchAction(
                MotionAction(MotionType.CANCEL, event)
            )
        }
    }
}