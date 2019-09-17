package com.example.newbiechen.nbreader.dl.module

import com.example.newbiechen.nbreader.data.local.room.dao.BookDao
import com.example.newbiechen.nbreader.ui.component.book.BookManager
import dagger.Binds
import dagger.Module
import dagger.Provides

/**
 *  author : newbiechen
 *  date : 2019-09-17 17:09
 *  description :
 */

@Module
class ReadModule {

    @Provides
    fun provideBookManager(bookDao: BookDao): BookManager {
        return BookManager(bookDao)
    }
}