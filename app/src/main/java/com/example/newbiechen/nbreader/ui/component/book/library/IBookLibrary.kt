package com.example.newbiechen.nbreader.ui.component.book.library

import com.example.newbiechen.nbreader.data.entity.BookEntity

/**
 *  author : newbiechen
 *  date : 2019-09-16 17:08
 *  description :用于调用 LibraryService 的接口
 */

interface IBookLibrary {
    // 查询操作
    fun getBookById(id: String): BookEntity

    fun getBookByPath(path: String): BookEntity
    fun getRecentBook(): BookEntity

    // 存储操作
    fun saveBook(book: BookEntity)

    // 移除操作
    fun removeBookById(id: String)
}