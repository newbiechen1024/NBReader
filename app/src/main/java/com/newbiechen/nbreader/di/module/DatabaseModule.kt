package com.newbiechen.nbreader.di.module

import android.content.Context
import androidx.room.Room
import com.newbiechen.nbreader.data.local.room.NBDataBase
import com.newbiechen.nbreader.data.local.room.dao.BookDao
import com.newbiechen.nbreader.data.local.room.dao.CatalogDao
import com.newbiechen.nbreader.uilts.Constants
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(context: Context): NBDataBase =
        Room.databaseBuilder(context, NBDataBase::class.java, Constants.DB_NAME).build()

    @Singleton
    @Provides
    fun provideCatalogDao(dataBase: NBDataBase): CatalogDao = dataBase.getCatalogDao()

    @Singleton
    @Provides
    fun provideBookDao(dataBase: NBDataBase): BookDao = dataBase.getBookDao()
}