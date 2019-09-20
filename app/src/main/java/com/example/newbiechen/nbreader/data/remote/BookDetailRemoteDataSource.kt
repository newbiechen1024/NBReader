package com.example.newbiechen.nbreader.data.remote

import com.example.newbiechen.nbreader.data.entity.NetBookDetailWrapper
import com.example.newbiechen.nbreader.data.remote.api.BookApi
import com.example.newbiechen.nbreader.data.repository.impl.IBookDetailRepository
import io.reactivex.Flowable
import javax.inject.Inject

/**
 *  author : newbiechen
 *  date : 2019-08-14 18:31
 *  description :
 */

class BookDetailRemoteDataSource @Inject constructor(private val api: BookApi) : IBookDetailRepository {
    override fun getBookDetail(bookId: String): Flowable<NetBookDetailWrapper> = api.getBookDetail(bookId)
}