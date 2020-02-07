package com.newbiechen.nbreader.data.repository

import com.newbiechen.nbreader.data.entity.NetBookDetailWrapper
import com.newbiechen.nbreader.data.repository.impl.IBookDetailRepository
import com.newbiechen.nbreader.di.annotation.qualifier.RemoteData
import io.reactivex.Flowable
import javax.inject.Inject

/**
 *  author : newbiechen
 *  date : 2019-08-14 18:33
 *  description :
 */

class BookDetailRepository @Inject constructor(@RemoteData private val remoteDataSource: IBookDetailRepository) :
    IBookDetailRepository {
    override fun getBookDetail(bookId: String): Flowable<NetBookDetailWrapper> = remoteDataSource.getBookDetail(bookId)
}