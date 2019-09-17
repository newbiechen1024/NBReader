package com.example.newbiechen.nbreader.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.newbiechen.nbreader.data.entity.CatalogEntity
import com.example.newbiechen.nbreader.data.local.room.dao.BookDao
import com.example.newbiechen.nbreader.data.local.room.dao.CatalogDao
import com.example.newbiechen.nbreader.ui.component.book.entity.Book

@Database(entities = [CatalogEntity::class, Book::class], version = 1)
abstract class NBDataBase : RoomDatabase() {
    abstract fun getCatalogDao(): CatalogDao
    abstract fun getBookDao(): BookDao
}