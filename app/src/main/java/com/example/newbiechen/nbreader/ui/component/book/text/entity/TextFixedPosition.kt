package com.example.newbiechen.nbreader.ui.component.book.text.entity

/**
 *  author : newbiechen
 *  date : 2019-10-28 20:25
 *  description :
 */

open class TextFixedPosition(
    private val chapterIndex: Int,
    private val paragraphIndex: Int,
    private val elementIndex: Int,
    private val charIndex: Int
) : TextPosition() {

    constructor(position: TextPosition) : this(
        position.getChapterIndex(),
        position.getParagraphIndex(),
        position.getElementIndex(),
        position.getCharIndex()
    )

    override fun getChapterIndex(): Int = chapterIndex

    override fun getParagraphIndex(): Int = paragraphIndex

    override fun getElementIndex(): Int = elementIndex

    override fun getCharIndex(): Int = charIndex
}
