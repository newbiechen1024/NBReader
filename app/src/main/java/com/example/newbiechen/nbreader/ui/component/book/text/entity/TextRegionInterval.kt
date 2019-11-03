package com.example.newbiechen.nbreader.ui.component.book.text.entity

import com.example.newbiechen.nbreader.ui.component.book.text.entity.element.TextWordElement

/**
 *  author : newbiechen
 *  date : 2019-10-30 19:40
 *  description :文本区域区间
 */

/**
 * @param paragraphIndex:段落索引
 * @param startElementIndex:起始元素区域
 * @param endElementIndex:终止元素区域
 */
abstract class TextRegionInterval(
    val paragraphIndex: Int,
    val startElementIndex: Int,
    val endElementIndex: Int
) : Comparable<TextRegionInterval> {

    /**
     * 区间是否包含该 Area
     */
    fun isContain(area: TextElementArea): Boolean {
        return compareTo(area) == 0
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is TextRegionInterval) {
            return false
        }
        val soul = other as TextRegionInterval?
        return paragraphIndex == soul!!.paragraphIndex &&
                startElementIndex == soul!!.startElementIndex &&
                endElementIndex == soul!!.endElementIndex
    }

    // 判断是否是包含关系
    override operator fun compareTo(interval: TextRegionInterval): Int {
        if (paragraphIndex != interval.paragraphIndex) {
            return if (paragraphIndex < interval.paragraphIndex) -1 else 1
        }
        if (endElementIndex < interval.startElementIndex) {
            return -1
        }
        //
        return if (startElementIndex > interval.endElementIndex) {
            1
        } else 0
    }

    operator fun compareTo(area: TextElementArea): Int {
        // 如果不是在统一段落
        if (paragraphIndex != area.getParagraphIndex()) {
            return if (paragraphIndex < area.getParagraphIndex()) -1 else 1
        }
        // 如果结束的位置区域小于当前区域
        if (endElementIndex < area.getElementIndex()) {
            return -1
        }
        // 如果起始区域大于当前区域
        return if (startElementIndex > area.getElementIndex()) {
            1
        } else 0
    }

    operator fun compareTo(position: TextPosition): Int {
        val ppi = position.getParagraphIndex()
        if (paragraphIndex != ppi) {
            return if (paragraphIndex < ppi) -1 else 1
        }
        val pei = position.getElementIndex()
        if (endElementIndex < pei) {
            return -1
        }
        return if (startElementIndex > pei) {
            1
        } else 0
    }
}

class TextWordRegionInterval(val wordElement: TextWordElement, position: TextPosition) : TextRegionInterval(
    position.getParagraphIndex(),
    position.getElementIndex(),
    position.getElementIndex()
) {
}

