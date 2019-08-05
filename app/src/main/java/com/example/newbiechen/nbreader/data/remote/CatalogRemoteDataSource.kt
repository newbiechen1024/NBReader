package com.example.newbiechen.nbreader.data.remote

import com.example.newbiechen.nbreader.data.entity.CatalogEntity
import com.example.newbiechen.nbreader.data.remote.api.BookApi
import com.example.newbiechen.nbreader.data.repository.impl.ICatalogRepository
import com.example.newbiechen.nbreader.uilts.Constants
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogRemoteDataSource @Inject constructor(private val bookApi: BookApi) : ICatalogRepository {

    override fun getCatalogItems(): Flowable<List<CatalogEntity>> {
        // 将获取到的 api 数据，解封装成一个 List
        return bookApi.getCatalog().map {
            ArrayList<CatalogEntity>().apply {
                addAll(it.male)
                addAll(it.female)
            }
        }
    }

    override fun saveCatalogItems(entities: List<CatalogEntity>) {
    }

}