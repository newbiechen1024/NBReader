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
        // 页面的数量
        private const val BITMAP_SIZE = 2
    }

    private var mPageWidth = 0
    private var mPageHeight = 0

    private var mBitmaps = arrayOfNulls<Bitmap>(BITMAP_SIZE)
    private var mBitmapTypes = arrayOfNulls<PageType>(BITMAP_SIZE)

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
        // TODO:CURRENT 可能会调用两次，导致崩溃。所以缓冲有问题
        if (type == PageType.CURRENT) {
            // 查找缓冲区中是否已存在该 type 的 page
            for (i in 0 until BITMAP_SIZE) {
                if (type == mBitmapTypes[i]) {
                    return mBitmaps[i]!!
                }
            }
        }

        // 查找可以用的 page
        var pageIndex = findAvailablePage()

        // 设置 bitmap 类型
        mBitmapTypes[pageIndex] = type

        if (mBitmaps[pageIndex] == null) {
            // 创建 bitmap
            mBitmaps[pageIndex] =
                Bitmap.createBitmap(mPageWidth, mPageHeight, Bitmap.Config.RGB_565)
        }

        pageListener.drawPage(Canvas(mBitmaps[pageIndex]!!), type)
        return mBitmaps[pageIndex]!!
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

    interface OnPageListener {
        // 是否页面存在
        fun hasPage(type: PageType): Boolean

        // 绘制页面
        fun drawPage(canvas: Canvas, type: PageType)

        // 通知翻页
        fun onTurnPage(pageType: PageType)
    }
}