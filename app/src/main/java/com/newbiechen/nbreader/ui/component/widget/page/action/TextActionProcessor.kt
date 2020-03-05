package com.newbiechen.nbreader.ui.component.widget.page.action

import android.view.MotionEvent
import com.newbiechen.nbreader.ui.component.widget.page.text.TextPageView
import com.newbiechen.nbreader.uilts.LogHelper

/**
 *  author : newbiechen
 *  date : 2019-08-30 16:20
 *  description :将点击事件转换成页面的行为事件
 */

typealias PageActionListener = (action: PageAction) -> Unit

class PageActionProcessor(private val textPageView: TextPageView) {

    companion object {
        private const val TAG = "PageActionProcessor"
    }

    private var mTouchEventProcessor =
        TextGestureDetector(textPageView.context, OnGestureCallback())

    private var mActionListener: PageActionListener? = null

    fun setPageActionListener(actionListener: PageActionListener) {
        mActionListener = actionListener
    }

    fun onTouchEvent(event: MotionEvent) {
        mTouchEventProcessor.onTouchEvent(event)
    }

    private inner class OnGestureCallback : TextGestureDetector.OnTextGestureListener {
        override fun onPress(event: MotionEvent) {
            dispatchAction(
                PressAction(event)
            )
        }

        override fun onMove(event: MotionEvent) {
            dispatchAction(
                MoveAction(event)
            )
        }

        override fun onRelease(event: MotionEvent) {
            dispatchAction(
                ReleaseAction(event)
            )
        }

        override fun onLongPress(event: MotionEvent) {
        }

        override fun onMoveAfterLongPress(event: MotionEvent) {
        }

        override fun onReleaseAfterLongPress(event: MotionEvent) {
        }

        override fun onSingleTap(event: MotionEvent) {
            dispatchAction(
                TapAction(event)
            )
        }

        override fun onDoubleTap(event: MotionEvent) {
        }

        override fun onCancelTap() {
            dispatchAction(
                CancelAction()
            )
        }

        internal fun dispatchAction(action: PageAction) {
            mActionListener?.invoke(action)
        }
    }
}