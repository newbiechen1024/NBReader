package com.example.newbiechen.nbreader.ui.component.book.text.entity

import com.example.newbiechen.nbreader.uilts.HashCodeCreator

/**
 *  author : newbiechen
 *  date : 2019-10-28 20:17
 *  description :指定在文本中的定位
 */

abstract class TextPosition : Comparable<TextPosition> {

    abstract fun getChapterIndex(): Int

    // 获取行的位置
    abstract fun getParagraphIndex(): Int

    // 获取某元素的位置
    abstract fun getElementIndex(): Int

    // 获取字节的位置
    abstract fun getCharIndex(): Int

    /**
     * 是否在相同的位置
     */
    fun isSamePosition(position: TextPosition): Boolean {
        return compareTo(position) == 0
    }

    override fun compareTo(other: TextPosition): Int {
        val c0 = getChapterIndex()
        val c1 = other.getChapterIndex()
        if (c0 != c1) {
            return if (c0 < c1) -1 else 1
        }

        val p0 = getParagraphIndex()
        val p1 = other.getParagraphIndex()
        if (p0 != p1) {
            return if (p0 < p1) -1 else 1
        }

        val e0 = getElementIndex()
        val e1 = other.getElementIndex()
        return if (e0 != e1) {
            if (e0 < e1) -1 else 1
        } else {
            getCharIndex() - other.getCharIndex()
        }
    }

    fun compareToIgnoreChar(position: TextPosition): Int {
        val c0 = getChapterIndex()
        val c1 = position.getChapterIndex()

        return if (c0 != c1) {
            if (c0 < c1) -1 else 1
        } else {

            val p0 = getParagraphIndex()
            val p1 = position.getParagraphIndex()

            if (p0 != p1) {
                if (p0 < p1) -1 else 1
            } else {
                getElementIndex() - position.getElementIndex()
            }
        }
    }

    /**
     * 返回定位快照
     */
    fun snapshot(): TextFixedPosition {
        return TextFixedPosition(this)
    }

    override fun hashCode(): Int {
        return HashCodeCreator.create()
            .addValue(getChapterIndex())
            .addValue(getParagraphIndex())
            .addValue(getElementIndex())
            .addValue(getCharIndex())
            .build()
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }

        if (other !is TextPosition) {
            return false
        }

        val position = other as TextPosition?
        return getChapterIndex() == position!!.getChapterIndex() &&
                getParagraphIndex() == position.getParagraphIndex() &&
                getElementIndex() == position.getElementIndex() &&
                getCharIndex() == position.getCharIndex()
    }

    override fun toString(): String {
        return javaClass.simpleName + " [" + getChapterIndex() + "," + getParagraphIndex() + "," + getElementIndex() + "," + getCharIndex() + "]"
    }
}