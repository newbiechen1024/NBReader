package com.example.newbiechen.nbreader.data.repository

import com.example.newbiechen.nbreader.data.entity.BookDetailWrapper
import com.example.newbiechen.nbreader.data.remote.BookListRemoteDataSource
import com.example.newbiechen.nbreader.data.repository.impl.IBookDetailRepository
import com.example.newbiechen.nbreader.dl.annotation.qualifier.RemoteData
import io.reactivex.Flowable
import javax.inject.Inject

/**
 *  author : newbiechen
 *  date : 2019-08-14 18:33
 *  description :
 */

class BookDetailRepository @Inject constructor(@RemoteData private val remoteDataSource: IBookDetailRepository) :
    IBookDetailRepository {
    override fun getBookDetail(bookId: String): Flowable<BookDetailWrapper> = remoteDataSource.getBookDetail(bookId)
}