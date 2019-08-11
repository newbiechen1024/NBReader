package com.example.newbiechen.nbreader.dl.module

import com.example.newbiechen.nbreader.data.local.CatalogLocalDataSource
import com.example.newbiechen.nbreader.data.remote.BookListRemoteDataSource
import com.example.newbiechen.nbreader.data.remote.CatalogRemoteDataSource
import com.example.newbiechen.nbreader.data.repository.BookListRepository
import com.example.newbiechen.nbreader.data.repository.CatalogRepository
import com.example.newbiechen.nbreader.data.repository.impl.IBookListRepository
import com.example.newbiechen.nbreader.data.repository.impl.ICatalogRepository
import com.example.newbiechen.nbreader.dl.annotation.qualifier.LocalData
import com.example.newbiechen.nbreader.dl.annotation.qualifier.RemoteData
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class RepositoryModule {

    @Singleton
    @LocalData
    @Binds
    abstract fun bindCatalogLocalDataSource(localDataSource: CatalogLocalDataSource): ICatalogRepository

    @Singleton
    @RemoteData
    @Binds
    abstract fun bindCatalogRemoteDataSource(remoteDataSource: CatalogRemoteDataSource): ICatalogRepository

    @Singleton
    @Binds
    abstract fun bindCatalogRepository(catalogRepository: CatalogRepository): ICatalogRepository

    @Singleton
    @RemoteData
    @Binds
    abstract fun bindBookListRemoteDataSource(remoteDataSource: BookListRemoteDataSource): IBookListRepository

    @Singleton
    @Binds
    abstract fun bindBookListRepository(bookListRepository: BookListRepository): IBookListRepository
}