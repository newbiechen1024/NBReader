package com.newbiechen.nbreader.di.module

import com.newbiechen.nbreader.di.annotation.scope.ActivityScoped
import com.newbiechen.nbreader.ui.page.bookshelf.BookShelfFragment
import com.newbiechen.nbreader.ui.page.find.FindFragment
import com.newbiechen.nbreader.ui.page.mine.MineFragment
import com.newbiechen.nbreader.uilts.factory.MainFragFactory
import com.newbiechen.nbreader.uilts.factory.NBMainFragFactory
import com.newbiechen.nbreader.di.annotation.scope.FragmentScoped
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