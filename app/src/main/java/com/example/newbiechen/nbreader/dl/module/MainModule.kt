package com.example.newbiechen.nbreader.dl.module

import com.example.newbiechen.nbreader.ui.page.bookshelf.BookShelfFragment
import com.example.newbiechen.nbreader.ui.page.find.FindFragment
import com.example.newbiechen.nbreader.ui.page.mine.MineFragment
import com.example.newbiechen.nbreader.uilts.factory.FragmentFactory
import com.example.newbiechen.nbreader.uilts.factory.NBFragmentFactory
import com.youtubedl.di.ActivityScoped
import com.youtubedl.di.FragmentScoped
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainModule {
    @ActivityScoped
    @Binds
    abstract fun bindFragmentFactory(fragmentFactory: NBFragmentFactory): FragmentFactory

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