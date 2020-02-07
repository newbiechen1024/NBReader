package com.newbiechen.nbreader.data.repository

import com.newbiechen.nbreader.data.entity.BookEntity
import com.newbiechen.nbreader.data.repository.impl.IBookRepository
import com.newbiechen.nbreader.di.annotation.qualifier.LocalData
import io.reactivex.Flowable
import javax.inject.Inject

/**
 *  author : newbiechen
 *  date : 2020-02-04 21:21
 *  description :书籍仓库
 */

class BookRepository @Inject constructor(
    @LocalData private val bookLocalDataSource: IBookRepository
) : IBookRepository {

    /**
     * 获取书籍
     * @param isCache：是否是缓存的书籍
     */
    override fun getBooks(isCache: Boolean): Flowable<List<BookEntity>> {
        return if (isCache) {
            // 从本地数据源获取书籍
            bookLocalDataSource.getBooks(true)
        } else {
            // 从网络中获取书籍，暂未实现
            Flowable.empty()
        }
    }

    /**
     * 存储书籍
     */
    override fun saveBook(bookEntity: BookEntity) {
        bookLocalDataSource.saveBook(bookEntity)
    }

    /**
     * 存储书籍列表
     */
    override fun saveBooks(books: List<BookEntity>) {
        bookLocalDataSource.saveBooks(books)
    }
}