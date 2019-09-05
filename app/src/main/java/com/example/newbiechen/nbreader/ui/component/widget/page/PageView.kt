package com.example.newbiechen.nbreader.ui.component.widget.page

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.newbiechen.nbreader.ui.component.widget.page.anim.NonePageAnimation
import com.example.newbiechen.nbreader.ui.component.widget.page.anim.PageAnimation
import com.example.newbiechen.nbreader.uilts.TouchProcessor

/**
 *  author : newbiechen
 *  date : 2019-08-30 16:26
 *  description :页面展示View
 */

class PageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mPageController = PageController()
    // 点击事件处理器
    private var mTouchProcessor = TouchProcessor(context, mPageController)
    // 页面管理器
    private var mPageManager = PageManager(mPageController)
    // 当前动画类型
    private var mPageAnimType = PageAnimType.NONE
    // 当前翻页动画
    private var mPageAnim: PageAnimation? = null

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mTouchProcessor.onTouchEvent(event!!)
        return true
    }

    /**
     * 设置页面动画类型
     */
    fun setPageAnim(type: PageAnimType) {
        mPageAnimType = type
    }

    private fun getPageAnim() {

    }

    override fun computeScroll() {
        super.computeScroll()
        // 处理翻页动画，滑动事件
    }
}