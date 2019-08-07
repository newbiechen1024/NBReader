package com.example.newbiechen.nbreader.data.repository

import com.example.newbiechen.nbreader.data.entity.BookListWrapper
import com.example.newbiechen.nbreader.data.repository.impl.IBookListRepository
import com.example.newbiechen.nbreader.dl.annotation.qualifier.RemoteData
import io.reactivex.Flowable
import javax.inject.Inject

/**
 *  author : newbiechen
 *  date : 2019-08-05 19:48
 *  description :书籍列表仓库
 */

class BookListRepository @Inject constructor(@RemoteData private val remoteDataSource: IBookListRepository) :
    IBookListRepository {
    override fun getBookList(
        alias: String,
        sort: Int,
        start: Int,
        limit: Int,
        cat: String?,
        isserial: Boolean?,
        updated: Int?
    ): Flowable<BookListWrapper> = remoteDataSource.getBookList(alias, sort, start, limit, cat, isserial, updated)
}

