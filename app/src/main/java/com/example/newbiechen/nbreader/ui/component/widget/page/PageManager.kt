package com.example.newbiechen.nbreader.ui.component.widget.page

import android.graphics.Bitmap
import android.graphics.Canvas
import com.example.newbiechen.nbreader.ui.component.widget.page.anim.IPageAnimCallback
import java.lang.RuntimeException

/**
 *  author : newbiechen
 *  date : 2020-01-26 16:18
 *  description :页面控制器
 */

class PageManager(private val pageListener: OnPageListener) : IPageAnimCallback {
    companion object {
        // 页面数量
        private const val BITMAP_SIZE = 2
    }

    private var mPageWidth = 0
    private var mPageHeight = 0

    private var mBitmaps = arrayOfNulls<Bitmap>(BITMAP_SIZE)

    override fun onPageSizeChanged(w: Int, h: Int) {
        if (mPageWidth != w || mPageHeight != h) {
            mPageWidth = w
            mPageHeight = h

            // 删除之前的 page 记录
            for (i in 0 until BITMAP_SIZE) {
                mBitmaps[i] = null
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
        // 检测数组 bitmap 是否存在
        if (mBitmaps[0] == null) {
            // 创建 bitmap
            mBitmaps[0] = Bitmap.createBitmap(mPageWidth, mPageHeight, Bitmap.Config.RGB_565)
        }

        // 获取 bitmap
        val page = mBitmaps[0]!!

        pageListener.drawPage(Canvas(page), type)

        // 交换图片缓冲 (本质上是实现 lru，但是翻页只需要 2 张图，所以用 swap 就行了)
        swapBitmap()

        return page
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
        // 通知回调
        pageListener.onTurnPage(if (isNext) PageType.NEXT else PageType.PREVIOUS)
    }

    interface OnPageListener {
        // 是否页面存在
        fun hasPage(type: PageType): Boolean

        // 绘制页面
        fun drawPage(canvas: Canvas, type: PageType)

        // 通知翻页
        fun onTurnPage(pageType: PageType)
    }
}