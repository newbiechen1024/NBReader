package com.newbiechen.nbreader.data.repository.impl

import com.newbiechen.nbreader.data.entity.BookEntity
import io.reactivex.Flowable

/**
 *  author : newbiechen
 *  date : 2020-02-04 20:56
 *  description :书籍仓库信息
 */

interface IBookRepository {
    /**
     * 根据参数获取书籍
     * @param isCache:是否是缓存书籍
     */
    fun getBooks(isCache: Boolean): Flowable<List<BookEntity>>

    /**
     * 存储书籍
     */
    fun saveBook(bookEntity: BookEntity)

    fun saveBooks(books: List<BookEntity>)
}