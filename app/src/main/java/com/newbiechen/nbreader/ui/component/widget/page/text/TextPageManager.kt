package com.newbiechen.nbreader.ui.component.widget.page.text

import android.graphics.Canvas
import android.graphics.Picture
import com.newbiechen.nbreader.ui.component.widget.page.PageType
import java.lang.RuntimeException

/**
 *  author : newbiechen
 *  date : 2020-01-27 10:59
 *  description :文本页面控制器
 */

class TextPageManager(private var pageListener: OnPageListener) {

    companion object {
        // 页面的数量
        private const val PICTURE_SIZE = 2
    }

    var pageWidth = 0
        private set
    var pageHeight = 0
        private set

    // 用 Picture 与 Bitmap 的 ARGB_8888 比较 (仅测试了一台机器，具体情况不好说，不管了先这么实现)
    // Picture 绘制效率略低于 Bitmap ARGB_8888，差不是很大
    // Picture 的内存占用小于 Bitmap ARGB_8888，差距明显
    // 所以采用 Picture 存储信息
    private var mPictures = arrayOfNulls<Picture>(PICTURE_SIZE)
    private var mPictureTypes = arrayOfNulls<PageType>(
        PICTURE_SIZE
    )

    fun onPageSizeChanged(w: Int, h: Int) {
        if (pageWidth != w || pageHeight != h) {
            pageWidth = w
            pageHeight = h
            // 删除之前的 page 记录
            for (i in 0 until PICTURE_SIZE) {
                mPictures[i] = null
                mPictureTypes[i] = null
            }

            // 通知页面改变
            pageListener.onPageSizeChanged(w, h)
        }
    }

    // 是否页面存在
    fun hasPage(type: PageType): Boolean {
        return pageListener.hasPage(type)
    }

    fun preparePage(type: PageType) {
        // getPage 会进行预加载操作
        getPage(type)
    }

    /**
     * 根据 type 获取具体的 page
     */
    fun getPage(type: PageType): Picture {
        // 查找缓冲区中是否已存在该 type 的 page
        for (i in 0 until PICTURE_SIZE) {
            if (type == mPictureTypes[i]) {
                return mPictures[i]!!
            }
        }

        var pageIndex = findAvailablePage()

        // 设置 bitmap 类型
        mPictureTypes[pageIndex] = type

        if (mPictures[pageIndex] == null) {
            // 创建 bitmap
            mPictures[pageIndex] = Picture()
        }

        val picture = mPictures[pageIndex]!!

        // 开始录制
        val canvas = picture.beginRecording(pageWidth, pageHeight)

        // 绘制页面
        pageListener.drawPage(canvas, type)

        // 停止录制
        picture.endRecording()

        return picture
    }

    // 查找可用的 page
    private fun findAvailablePage(): Int {
        // 查找是否存在未使用的图片
        for (i in 0 until PICTURE_SIZE) {
            if (mPictureTypes[i] == null) {
                return i
            }
        }

        // 查找非 current 的图片
        for (i in 0 until PICTURE_SIZE) {
            if (PageType.CURRENT != mPictureTypes[i]) {
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
    fun turnPage(pageType: PageType) {
        // 如果对自身翻页不处理
        if (pageType == PageType.CURRENT) {
            return
        }

        for (i in 0 until PICTURE_SIZE) {
            if (mPictureTypes[i] == null) {
                continue
            }

            mPictureTypes[i] = when (pageType) {
                PageType.PREVIOUS -> mPictureTypes[i]!!.getNext()
                PageType.NEXT -> mPictureTypes[i]!!.getPrevious()
                PageType.CURRENT -> mPictureTypes[i]
            }
        }

        // 通知回调
        pageListener.onTurnPage(pageType)
    }

    /**
     * 重置页面
     */
    fun resetPages() {
        // 删除之前的 page 记录
        for (i in 0 until PICTURE_SIZE) {
            mPictureTypes[i] = null
        }
    }

    interface OnPageListener {
        fun onPageSizeChanged(width: Int, height: Int)

        // 通知翻页
        fun onTurnPage(pageType: PageType)

        // 是否页面存在
        fun hasPage(type: PageType): Boolean

        // 绘制页面
        fun drawPage(canvas: Canvas, type: PageType)
    }
}