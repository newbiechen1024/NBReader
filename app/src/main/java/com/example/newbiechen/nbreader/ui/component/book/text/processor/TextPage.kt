package com.example.newbiechen.nbreader.ui.component.book.text.processor

import com.example.newbiechen.nbreader.ui.component.book.text.entity.TextLineInfo

/**
 *  author : newbiechen
 *  date : 2019-10-22 15:26
 *  description :
 */

class TextPage {
    /**
     *  公共参数
     */
    var pageWidth: Int = 0
        private set
    var pageHeight: Int = 0
        private set
    var pageState: State = State.NONE
        private set
    // 页面的起始光标
    var startWordCursor: TextWordCursor? = null
        private set
    // 页面的结束光标
    var endWordCursor: TextWordCursor? = null
        private set

    // 存储 Page 包含的行信息
    val lineInfoList: ArrayList<TextLineInfo> = ArrayList()

    // 存储元素展示区域信息
    val textElementAreaVector = TextElementAreaVector()
    // 页面状态
    private var mPageState = State.NONE

    /**
     * 设置 Page 的宽高
     */
    fun setViewPort(pageWidth: Int, pageHeight: Int) {
        this.pageWidth = pageWidth
        this.pageHeight = pageHeight
    }

    /**
     * 设置页面状态
     * @return 表示是否设置成功
     */
    fun setPageState(pageState: State): Boolean {
        if (mPageState == pageState) {
            return true
        }
        when (pageState) {
            State.NONE -> { // 回到未准备状态
                reset()
                return true
            }
            State.KNOW_START_CURSOR, State.KNOW_END_CURSOR -> {
                // 由于光标改变，则之前的行也不能用了
                lineInfoList.clear()
                // 只有同种类型的 cursor 才能设置状态
                if (startWordCursor != null) {
                    mPageState = State.KNOW_START_CURSOR
                    return true
                } else if (endWordCursor != null) {
                    // 如果终止光标存在
                    mPageState = State.KNOW_END_CURSOR
                    return true
                }
            }
            State.PREPARED -> {
                if (startWordCursor != null
                    && endWordCursor != null
                ) {
                    mPageState = State.PREPARED
                    return true
                }
            }
        }
        return false
    }

    /**
     * 初始化页面的光标
     * @param wordCursor:设置光标位置
     * @param isStart:传入的是页面的起始光标，还是结尾光标
     */
    fun initCursor(wordCursor: TextWordCursor, isStart: Boolean) {
        // 重置当前状态
        setPageState(State.NONE)

        // 起始光标和末尾光标同时指向起始光标
        if (startWordCursor == null) {
            startWordCursor = TextWordCursor(wordCursor)
        } else {
            startWordCursor!!.updateCursor(wordCursor)
        }

        if (endWordCursor == null) {
            endWordCursor = TextWordCursor(wordCursor)
        } else {
            endWordCursor!!.updateCursor(wordCursor)
        }

        setPageState(if (isStart) State.KNOW_START_CURSOR else State.KNOW_END_CURSOR)
    }




    /**
     * 重置 Page 数据信息
     */
    fun reset() {
        lineInfoList.clear()
        startWordCursor = null
        endWordCursor = null
        mPageState = State.NONE
    }

    // 当前页面的状态
    enum class State {
        NONE,
        // 当前 Page 只包含起始光标
        KNOW_START_CURSOR,
        // 当前 Page 只包含结束光标
        KNOW_END_CURSOR,
        // 当前页面已准备完成
        PREPARED
    }
}