package com.example.newbiechen.nbreader.dl.module

import com.example.newbiechen.nbreader.data.local.BookLocalDataSource
import com.example.newbiechen.nbreader.data.local.CatalogLocalDataSource
import com.example.newbiechen.nbreader.data.remote.BookDetailRemoteDataSource
import com.example.newbiechen.nbreader.data.remote.BookListRemoteDataSource
import com.example.newbiechen.nbreader.data.remote.CatalogRemoteDataSource
import com.example.newbiechen.nbreader.data.repository.BookDetailRepository
import com.example.newbiechen.nbreader.data.repository.BookListRepository
import com.example.newbiechen.nbreader.data.repository.BookRepository
import com.example.newbiechen.nbreader.data.repository.CatalogRepository
import com.example.newbiechen.nbreader.data.repository.impl.IBookDetailRepository
import com.example.newbiechen.nbreader.data.repository.impl.IBookListRepository
import com.example.newbiechen.nbreader.data.repository.impl.IBookRepository
import com.example.newbiechen.nbreader.data.repository.impl.ICatalogRepository
import com.example.newbiechen.nbreader.dl.annotation.qualifier.LocalData
import com.example.newbiechen.nbreader.dl.annotation.qualifier.RemoteData
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class RepositoryModule {

    @Singleton
    @Binds
    @LocalData
    abstract fun bindCatalogLocalDataSource(localDataSource: CatalogLocalDataSource): ICatalogRepository

    @Singleton
    @Binds
    @RemoteData
    abstract fun bindCatalogRemoteDataSource(remoteDataSource: CatalogRemoteDataSource): ICatalogRepository

    @Singleton
    @Binds
    abstract fun bindCatalogRepository(catalogRepository: CatalogRepository): ICatalogRepository

    @Singleton
    @Binds
    @RemoteData
    abstract fun bindBookListRemoteDataSource(remoteDataSource: BookListRemoteDataSource): IBookListRepository

    @Singleton
    @Binds
    abstract fun bindBookListRepository(bookListRepository: BookListRepository): IBookListRepository

    @Singleton
    @Binds
    @RemoteData
    abstract fun bindBookDetailRemoteDataSource(remoteDataSource: BookDetailRemoteDataSource): IBookDetailRepository

    @Singleton
    @Binds
    abstract fun bindBookBookDetailRepository(bookDetailRepository: BookDetailRepository): IBookDetailRepository

    @Singleton
    @Binds
    @LocalData
    abstract fun bindBookLocalDataSource(localDataSource: BookLocalDataSource): IBookRepository

    @Singleton
    @Binds
    abstract fun bindBookRepository(bookRepository: BookRepository): IBookRepository
}