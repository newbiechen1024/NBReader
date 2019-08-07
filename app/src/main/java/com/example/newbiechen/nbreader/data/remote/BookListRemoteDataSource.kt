package com.example.newbiechen.nbreader.data.remote

import com.example.newbiechen.nbreader.data.entity.BookListWrapper
import com.example.newbiechen.nbreader.data.remote.api.BookApi
import com.example.newbiechen.nbreader.data.repository.impl.IBookListRepository
import io.reactivex.Flowable
import javax.inject.Inject

/**
 *  author : newbiechen
 *  date : 2019-08-05 19:47
 *  description :
 */

class BookListRemoteDataSource @Inject constructor(private val api: BookApi) : IBookListRepository {
    override fun getBookList(
        alias: String,
        sort: Int,
        start: Int,
        limit: Int,
        cat: String?,
        isserial: Boolean?,
        updated: Int?
    ): Flowable<BookListWrapper> = api.getBookList(alias, sort, cat, isserial, updated, start, limit)
}