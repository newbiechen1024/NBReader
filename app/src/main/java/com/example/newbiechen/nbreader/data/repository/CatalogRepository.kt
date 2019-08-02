package com.example.newbiechen.nbreader.data.repository

import com.example.newbiechen.nbreader.data.entity.CatalogEntity
import com.example.newbiechen.nbreader.data.repository.impl.ICatalogRepository
import com.example.newbiechen.nbreader.dl.annotation.qualifier.LocalData
import com.example.newbiechen.nbreader.dl.annotation.qualifier.RemoteData
import io.reactivex.Flowable
import javax.inject.Inject

class CatalogRepository @Inject constructor(
    @LocalData private val localDataSource: ICatalogRepository,
    @RemoteData private val remoteDataSource: ICatalogRepository
) : ICatalogRepository {

    private var cacheCatalogList = listOf<CatalogEntity>()

    override fun getCatalogItems(): Flowable<List<CatalogEntity>> {
        // 判断当前缓存是否存在
        return if (cacheCatalogList.isNotEmpty()) {
            Flowable.just(cacheCatalogList)
        } else {
            // 从本地请求
            getAndCacheLocal().flatMap {
                // 如果本地请求没有数据，则走网络请求
                if (it.isEmpty()) {
                    getAndCacheRemote()
                } else {
                    Flowable.just(it)
                }
            }
        }
    }

    private fun getAndCacheLocal(): Flowable<List<CatalogEntity>> {
        return localDataSource.getCatalogItems()
            .doOnNext {
                cacheCatalogList = it
            }
    }

    private fun getAndCacheRemote(): Flowable<List<CatalogEntity>> {
        return remoteDataSource.getCatalogItems()
            .doOnNext {
                localDataSource.saveCatalogItems(it)
                cacheCatalogList = it
            }
    }

    override fun saveCatalogItems(entities: List<CatalogEntity>) {
        // 存储数据到本地
    }
}