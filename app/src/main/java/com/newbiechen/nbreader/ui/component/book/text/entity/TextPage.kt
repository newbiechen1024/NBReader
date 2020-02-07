package com.newbiechen.nbreader.ui.component.book.text.entity

import com.newbiechen.nbreader.ui.component.book.text.processor.TextElementAreaVector
import com.newbiechen.nbreader.ui.component.book.text.processor.cursor.TextWordCursor

/**
 *  author : newbiechen
 *  date : 2020-01-31 10:01
 *  description :文本页面信息
 */

/**
 * @param pageStartCursor:页面起始光标
 * @param pageEndCursor:页面结尾光标
 */
class TextPage(pageStartCursor: TextWordCursor, pageEndCursor: TextWordCursor) {

    companion object {
        private const val TAG = "TextPage"
    }

    // 外部传入的 cursor 不是快照，可能会改变数据，所以需要重新创建一个对象
    val startWordCursor = TextWordCursor(pageStartCursor)

    val endWordCursor = TextWordCursor(pageEndCursor)

    // 是否页面光标转化为了行数据
    var isPrepare = false

    // 页面中存储的行数据
    val textLineList: ArrayList<TextLine> = ArrayList()

    // 页面中每个元素的展示区域
    val textElementAreaVector = TextElementAreaVector()

    /**
     * 重置 Page 数据信息
     */
    fun reset() {
        textLineList.clear()
        textElementAreaVector.clear()
        isPrepare = false
    }
}