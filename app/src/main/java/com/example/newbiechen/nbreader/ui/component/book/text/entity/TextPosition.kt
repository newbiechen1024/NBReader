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

    override fun compareTo(position: TextPosition): Int {
        val p0 = getParagraphIndex()
        val p1 = position.getParagraphIndex()
        if (p0 != p1) {
            return if (p0 < p1) -1 else 1
        }

        val e0 = getElementIndex()
        val e1 = position.getElementIndex()
        return if (e0 != e1) {
            if (e0 < e1) -1 else 1
        } else getCharIndex() - position.getCharIndex()
    }

    fun compareToIgnoreChar(position: TextPosition): Int {
        val p0 = getParagraphIndex()
        val p1 = position.getParagraphIndex()
        return if (p0 != p1) {
            if (p0 < p1) -1 else 1
        } else getElementIndex() - position.getElementIndex()

    }

    override fun hashCode(): Int {
        return HashCodeCreator.create()
            .addValue(getChapterIndex())
            .addValue(getParagraphIndex())
            .addValue(getElementIndex())
            .addValue(getCharIndex())
            .build()
    }

    override fun equals(obj: Any?): Boolean {
        if (obj === this) {
            return true
        }

        if (obj !is TextPosition) {
            return false
        }

        val position = obj as TextPosition?
        return getChapterIndex() == position!!.getChapterIndex() &&
                getParagraphIndex() == position!!.getParagraphIndex() &&
                getElementIndex() == position!!.getElementIndex() &&
                getCharIndex() == position!!.getCharIndex()
    }

    override fun toString(): String {
        return javaClass.simpleName + " [" + getChapterIndex() + "," + getParagraphIndex() + "," + getElementIndex() + "," + getCharIndex() + "]"
    }
}