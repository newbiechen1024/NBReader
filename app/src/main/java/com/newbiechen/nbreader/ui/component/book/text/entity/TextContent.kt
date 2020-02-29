package com.newbiechen.nbreader.ui.component.book.text.entity

/**
 *  author : newbiechen
 *  date : 2020/2/29 10:47 AM
 *  description :文本内容数据
 */
data class TextContent(
    // 资源数组信息
    val resourceData: ByteArray?,
    // 内容数组信息
    val contentData: ByteArray
)