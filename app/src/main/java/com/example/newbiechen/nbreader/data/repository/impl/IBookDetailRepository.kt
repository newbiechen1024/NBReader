package com.example.newbiechen.nbreader.data.repository.impl

import com.example.newbiechen.nbreader.data.entity.BookDetailWrapper
import io.reactivex.Flowable

/**
 *  author : newbiechen
 *  date : 2019-08-14 18:28
 *  description :
 */

interface IBookDetailRepository {
    fun getBookDetail(bookId: String): Flowable<BookDetailWrapper>
}