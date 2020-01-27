package com.example.newbiechen.nbreader.ui.component.widget.page.action

import android.view.MotionEvent
import com.example.newbiechen.nbreader.ui.component.widget.page.PageTextView

/**
 *  author : newbiechen
 *  date : 2019-08-30 16:20
 *  description :将点击事件转换成页面的行为事件
 */

typealias PageActionListener = (action: PageAction) -> Unit

class PageActionProcessor(private val pageTextView: PageTextView) {

    companion object {
        private const val TAG = "PageController"
    }

    private var mTouchEventProcessor =
        TouchEventProcessor(pageTextView.context, OnTouchEventCallback())

    private var mActionListener: PageActionListener? = null

    fun setPageActionListener(actionListener: PageActionListener) {
        mActionListener = actionListener
    }

    fun onTouchEvent(event: MotionEvent) {
        mTouchEventProcessor.onTouchEvent(event)
    }

    private inner class OnTouchEventCallback : TouchEventProcessor.OnTouchEventListener {
        override fun onPress(x: Int, y: Int) {
            dispatchAction(
                PressAction(x, y)
            )
        }

        override fun onMove(x: Int, y: Int) {
            dispatchAction(
                MoveAction(x, y)
            )
        }

        override fun onRelease(x: Int, y: Int) {
            dispatchAction(
                ReleaseAction(x, y)
            )
        }

        override fun onLongPress(x: Int, y: Int) {
        }

        override fun onMoveAfterLongPress(x: Int, y: Int) {
        }

        override fun onReleaseAfterLongPress(x: Int, y: Int) {
        }

        override fun onSingleTap(x: Int, y: Int) {
            dispatchAction(
                TapAction(x, y)
            )
        }

        override fun onDoubleTap(x: Int, y: Int) {

        }

        override fun onCancelTap() {
            // TODO:处理 cancel 的情况
        }

        internal fun dispatchAction(action: PageAction) {
            mActionListener?.invoke(action)
        }
    }
}