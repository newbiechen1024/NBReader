package com.example.newbiechen.nbreader.ui.component.book.text.entity.element

/**
 *  author : newbiechen
 *  date : 2019-10-26 15:47
 *  description :文本控制符元素
 */

data class TextControlElement(
    val styleType: Byte, // control 持有了 style 类型
    val isStart: Boolean // 是起始控制标签还是结尾控制标签
) : TextElement() {
    companion object {
        // 缓存 ControlElement

        // 起始元素数组
        private val myStartElements = arrayOfNulls<TextControlElement>(256)
        // 终止元素数组
        private val myEndElements = arrayOfNulls<TextControlElement>(256)

        fun get(type: Byte, isStart: Boolean): TextControlElement {
            // 根据 start 决定数组类型
            val elements = if (isStart) myStartElements else myEndElements
            // byte 的取值范围在 -128 ~ 127 之间
            // byte 转 int，java 是在用补码的形式，也就是说 byte 值为 -1，则其二进制为 1111 1111 (经过、原码、反码、补码)，那么转 int 时会造成
            // 1111 1111 1111 1111，这种补位就会造成误差。和0xff相与后，高24比特就会被清0了，结果就对了。即 0000 0000 1111 1111
            // 原因：https://my.oschina.net/andyfeng/blog/1621012
            var element: TextControlElement? = elements[type.toInt() and 0xFF]

            // 如果 element 不存在
            if (element == null) {
                // 创建一个 element
                element = TextControlElement(type, isStart)
                elements[type.toInt() and 0xFF] = element
            }

            return element
        }
    }
}