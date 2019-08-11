package com.example.newbiechen.nbreader.dl.module.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newbiechen.nbreader.dl.annotation.key.ViewModelKey
import com.example.newbiechen.nbreader.ui.page.booklist.BookListViewModel
import com.example.newbiechen.nbreader.ui.page.find.FindViewModel
import com.example.newbiechen.nbreader.ui.page.main.MainViewModel
import com.example.newbiechen.nbreader.uilts.factory.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
abstract class ViewModelModule {

    @Singleton
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @IntoMap
    @Binds
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(model: MainViewModel): ViewModel

    @IntoMap
    @Binds
    @ViewModelKey(FindViewModel::class)
    abstract fun bindFindViewModel(model: FindViewModel): ViewModel

    @IntoMap
    @Binds
    @ViewModelKey(BookListViewModel::class)
    abstract fun bindBookListViewModel(model: BookListViewModel): ViewModel
}