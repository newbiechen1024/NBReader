package com.example.newbiechen.nbreader.data.repository.impl

import com.example.newbiechen.nbreader.data.entity.NetBookDetailWrapper
import io.reactivex.Flowable

/**
 *  author : newbiechen
 *  date : 2019-08-14 18:28
 *  description :书籍详细信息仓库 (NetBookDetail)
 */

interface IBookDetailRepository {
    fun getBookDetail(bookId: String): Flowable<NetBookDetailWrapper>
}