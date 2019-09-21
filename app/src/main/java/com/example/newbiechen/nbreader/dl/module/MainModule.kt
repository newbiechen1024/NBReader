package com.example.newbiechen.nbreader.dl.module

import com.example.newbiechen.nbreader.dl.annotation.scope.ActivityScoped
import com.example.newbiechen.nbreader.ui.page.bookshelf.BookShelfFragment
import com.example.newbiechen.nbreader.ui.page.find.FindFragment
import com.example.newbiechen.nbreader.ui.page.mine.MineFragment
import com.example.newbiechen.nbreader.uilts.factory.MainFragFactory
import com.example.newbiechen.nbreader.uilts.factory.NBMainFragFactory
import com.example.newbiechen.nbreader.dl.annotation.scope.FragmentScoped
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainModule {
    @ActivityScoped
    @Binds
    abstract fun bindFragmentFactory(fragmentFactory: NBMainFragFactory): MainFragFactory

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bindBookShelfFragment(): BookShelfFragment

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bindFindFragment(): FindFragment

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bindMineFragment(): MineFragment
}