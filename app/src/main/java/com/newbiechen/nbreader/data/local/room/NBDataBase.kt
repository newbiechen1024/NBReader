package com.newbiechen.nbreader.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.newbiechen.nbreader.data.entity.CatalogEntity
import com.newbiechen.nbreader.data.entity.BookEntity
import com.newbiechen.nbreader.data.local.room.dao.BookDao
import com.newbiechen.nbreader.data.local.room.dao.CatalogDao

@Database(entities = [CatalogEntity::class, BookEntity::class], version = 1)
abstract class NBDataBase : RoomDatabase() {
    abstract fun getCatalogDao(): CatalogDao
    abstract fun getBookDao(): BookDao
}