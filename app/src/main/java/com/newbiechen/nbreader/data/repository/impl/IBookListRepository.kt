package com.newbiechen.nbreader.data.repository.impl

import com.newbiechen.nbreader.data.entity.NetBookListWrapper
import io.reactivex.Flowable

/**
 *  author : newbiechen
 *  date : 2019-08-05 19:41
 *  description :书籍列表信息仓库 (NetBookPage)
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
    ): Flowable<NetBookListWrapper>
}