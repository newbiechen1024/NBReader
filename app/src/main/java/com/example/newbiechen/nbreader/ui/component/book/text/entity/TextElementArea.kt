package com.example.newbiechen.nbreader.ui.component.book.text.entity

import com.example.newbiechen.nbreader.ui.component.book.text.entity.element.TextElement
import com.example.newbiechen.nbreader.ui.component.book.text.entity.textstyle.TextStyle

/**
 *  author : newbiechen
 *  date : 2019-10-28 20:30
 *  description :文本元素绘制区域数据
 */

/**
 * @param paragraphIndex:段落索引
 * @param elementIndex:元素索引
 * @param charIndex:字节索引
 * @param length:
 * @param isLastElement:is last in element
 * @param addHyphenationSign: add hyphenation sign
 * @param style:元素样式
 * @param element:元素
 * @param startX:元素绘制区域的左上角点
 * @param endY:元素绘制区域的右下角点
 */
class TextElementArea(
    paragraphIndex: Int,
    elementIndex: Int,
    charIndex: Int,
    val length: Int,
    val isLastElement: Boolean,
    val addHyphenationSign: Boolean,
    val isStyleChange: Boolean,
    val style: TextStyle,
    val element: TextElement,
    val startX: Int,
    val startY: Int,
    val endX: Int,
    val endY: Int
) : TextFixedPosition(paragraphIndex, elementIndex, charIndex)