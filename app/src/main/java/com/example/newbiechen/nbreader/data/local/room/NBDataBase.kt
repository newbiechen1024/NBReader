package com.example.newbiechen.nbreader.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.newbiechen.nbreader.data.entity.CatalogEntity
import com.example.newbiechen.nbreader.data.local.room.dao.CatalogDao

@Database(entities = [CatalogEntity::class], version = 1)
abstract class NBDataBase : RoomDatabase() {
    abstract fun getCatalogDao(): CatalogDao
}