package com.example.newbiechen.nbreader.data.entity

import com.example.newbiechen.nbreader.ui.component.book.type.BookType

/**
 *  author : newbiechen
 *  date : 2020-02-04 20:42
 *  description:本地书籍信息
 */

data class LocalBookEntity(
    val id: Long,
    val name: String, // 书籍名
    val type: BookType, // 书籍类型
    val size: Long, // 书籍大小
    val lastModified: Long, // 文件的添加时间，单位:ms
    val path: String // 文件路径
) {
    override fun toString(): String {
        return "BookInfo(id=$id, title='$name', type=$type, size=$size, lastModified=$lastModified, path='$path')"
    }
}
