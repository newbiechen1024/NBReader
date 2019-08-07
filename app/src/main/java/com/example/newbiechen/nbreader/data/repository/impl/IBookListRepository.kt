package com.example.newbiechen.nbreader.data.repository.impl

import com.example.newbiechen.nbreader.data.entity.BookListWrapper
import io.reactivex.Flowable

/**
 *  author : newbiechen
 *  date : 2019-08-05 19:41
 *  description :
 */

interface IBookListRepository {
    fun getBookList(
        alias: String,
        sort: Int,
        start: Int,
        limit: Int,
        cat: String? = null,
        isserial: Boolean? = null,
        updated: Int? = null
    ): Flowable<BookListWrapper>
}