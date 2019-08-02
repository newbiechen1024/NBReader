package com.example.newbiechen.nbreader.data.local.room.dao

import androidx.room.*
import com.example.newbiechen.nbreader.data.entity.CatalogEntity
import io.reactivex.Maybe

@Dao
interface CatalogDao {
    @Query("SELECT * FROM catalog_entity")
    fun getAllCatalogs(): Maybe<List<CatalogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCatalogs(catalogs: List<CatalogEntity>)

    @Delete
    fun deleteAllCatalogs(catalogs: List<CatalogEntity>)
}