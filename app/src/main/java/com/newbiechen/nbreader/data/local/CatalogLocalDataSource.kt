package com.newbiechen.nbreader.data.local

import com.newbiechen.nbreader.data.entity.CatalogEntity
import com.newbiechen.nbreader.data.local.room.dao.CatalogDao
import com.newbiechen.nbreader.data.repository.impl.ICatalogRepository
import io.reactivex.Flowable
import javax.inject.Inject

class CatalogLocalDataSource @Inject constructor(private val catalogDao: CatalogDao) : ICatalogRepository {

    override fun getCatalogItems(): Flowable<List<CatalogEntity>> = catalogDao.getAllCatalogs().toFlowable()

    override fun saveCatalogItems(entities: List<CatalogEntity>) {
        catalogDao.insertCatalogs(entities)
    }
}