package com.example.newbiechen.nbreader.data.local

import com.example.newbiechen.nbreader.data.entity.BookEntity
import com.example.newbiechen.nbreader.data.local.room.dao.BookDao
import com.example.newbiechen.nbreader.data.repository.impl.IBookRepository
import io.reactivex.Flowable
import javax.inject.Inject

/**
 *  author : newbiechen
 *  date : 2020-02-04 20:54
 *  description :书籍本地缓存数据源
 */

class BookLocalDataSource @Inject constructor(private val bookDao: BookDao) : IBookRepository {

    override fun getBooks(isCache: Boolean): Flowable<List<BookEntity>> {
        // 如果不是从缓存中读取的，则直接返回
        if (!isCache) {
            return Flowable.empty()
        }

        return bookDao.getAllBooks().toFlowable()
    }

    override fun saveBook(bookEntity: BookEntity) {
        bookDao.insertBook(bookEntity)
    }

    override fun saveBooks(books: List<BookEntity>) {
        bookDao.insertBooks(books)
    }
}