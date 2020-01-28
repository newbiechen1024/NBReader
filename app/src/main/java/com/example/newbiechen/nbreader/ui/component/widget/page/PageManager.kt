package com.example.newbiechen.nbreader.ui.component.widget.page

import android.graphics.Bitmap
import android.graphics.Canvas
import com.example.newbiechen.nbreader.ui.component.widget.page.anim.IPageAnimCallback
import com.example.newbiechen.nbreader.ui.component.widget.page.anim.PageAnimListener
import com.example.newbiechen.nbreader.uilts.LogHelper
import java.lang.RuntimeException

/**
 *  author : newbiechen
 *  date : 2020-01-26 16:18
 *  description :页面控制器
 */

class PageManager(private val pageListener: OnPageListener) : IPageAnimCallback, PageAnimListener {
    companion object {
        // 页面数量
        private const val BITMAP_SIZE = 2
        private const val TAG = "PageManager"
    }

    private var mPageWidth = 0
    private var mPageHeight = 0

    private var mBitmaps = arrayOfNulls<Bitmap>(BITMAP_SIZE)
    private var mBitmapTypes = arrayOfNulls<PageType>(BITMAP_SIZE)

    // 是否动画正在运行
    private var isAnimRunning = false


    override fun onPageSizeChanged(w: Int, h: Int) {
        if (mPageWidth != w || mPageHeight != h) {
            mPageWidth = w
            mPageHeight = h

            // 删除之前的 page 记录
            for (i in 0 until BITMAP_SIZE) {
                mBitmaps[i] = null
                mBitmapTypes[i] = null
            }
        }
    }

    // 是否页面存在
    override fun hasPage(type: PageType): Boolean {
        return pageListener.hasPage(type)
    }

    /**
     * 根据 type 获取具体的 page
     */
    override fun getPage(type: PageType): Bitmap {

        LogHelper.i(TAG, "getPage: $type")

        var pageIndex = 0

        // 如果动画正在运行，采用方案一进行缓存
        if (isAnimRunning) {
            // 查找缓冲区中是否已存在该 type 的 page
            for (i in 0 until BITMAP_SIZE) {
                if (type == mBitmapTypes[i]) {
                    return mBitmaps[i]!!
                }
            }

            pageIndex = findAvailablePage()

            // 设置 bitmap 类型
            mBitmapTypes[pageIndex] = type
        }

        // 检测数组 bitmap 是否存在
        if (mBitmaps[pageIndex] == null) {
            // 创建 bitmap
            mBitmaps[pageIndex] =
                Bitmap.createBitmap(mPageWidth, mPageHeight, Bitmap.Config.ARGB_8888)
        }

        // 获取 bitmap
        val page = mBitmaps[pageIndex]!!

        pageListener.drawPage(Canvas(page), type)

        // 方案 2 处理
        if (!isAnimRunning) {
            // 交换图片缓冲 (本质上是实现 lru，但是翻页只需要 2 张图，所以用 swap 就行了)
            swapBitmap()
        }
        return page
    }


    // 查找可用的 page
    private fun findAvailablePage(): Int {
        // 查找是否存在未使用的图片
        for (i in 0 until BITMAP_SIZE) {
            if (mBitmapTypes[i] == null) {
                return i
            }
        }

        // 查找非 current 的图片
        for (i in 0 until BITMAP_SIZE) {
            if (PageType.CURRENT != mBitmapTypes[i]) {
                return i
            }
        }

        // 如果都不存在，则表示代码有问题
        throw RuntimeException("code error")
    }

    private fun swapBitmap() {
        val temp = mBitmaps[1]
        mBitmaps[1] = mBitmaps[0]
        mBitmaps[0] = temp
    }

    /**
     * 对 page 进行翻页操作
     * @param isNext:是否翻到下一页
     */
    override fun turnPage(isNext: Boolean) {
        for (i in 0 until BITMAP_SIZE) {
            if (mBitmapTypes[i] == null) {
                continue
            }
            mBitmapTypes[i] =
                if (isNext) mBitmapTypes[i]!!.getPrevious() else mBitmapTypes[i]!!.getNext()
        }

        // 通知回调
        pageListener.onTurnPage(if (isNext) PageType.NEXT else PageType.PREVIOUS)
    }

    /**
     * 重置页面
     */
    private fun resetPage() {
        // 删除之前的 page 记录
        for (i in 0 until BITMAP_SIZE) {
            mBitmapTypes[i] = null
        }
    }

    interface OnPageListener {
        // 是否页面存在
        fun hasPage(type: PageType): Boolean

        // 绘制页面
        fun drawPage(canvas: Canvas, type: PageType)

        // 通知翻页
        fun onTurnPage(pageType: PageType)
    }

    override fun onAnimationStart() {
        isAnimRunning = true

        resetPage()

        // 将最后绘制的 bitmap 作为 animRunning 状态的 current page (优化绘制效果)
        if (mBitmaps[1] != null) {
            mBitmapTypes[1] = PageType.CURRENT
        }
    }

    override fun onAnimationEnd() {
        isAnimRunning = false
    }
}