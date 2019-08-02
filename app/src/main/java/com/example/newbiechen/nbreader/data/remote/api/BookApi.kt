package com.example.newbiechen.nbreader.data.remote.api

import com.example.newbiechen.nbreader.data.entity.CatalogWrapper
import io.reactivex.Flowable
import retrofit2.http.GET

interface BookApi {

    /**
     * 获取主目录
     *
     * @return
     */
    @GET("/cats/lv2/statistics")
    fun getCatalog(): Flowable<CatalogWrapper>
}