package com.newbiechen.nbreader.di.module.base

import com.newbiechen.nbreader.di.annotation.scope.ActivityScoped
import com.newbiechen.nbreader.di.module.FileSystemModule
import com.newbiechen.nbreader.di.module.MainModule
import com.newbiechen.nbreader.ui.page.bookdetail.BookDetailActivity
import com.newbiechen.nbreader.ui.page.booklist.BookListActivity
import com.newbiechen.nbreader.ui.page.filesystem.FileSystemActivity
import com.newbiechen.nbreader.ui.page.main.MainActivity
import com.newbiechen.nbreader.ui.page.read.ReadActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * 所有继承 BaseBindingActivity 的 Activity 都需要在这里注册
 */
@Module
internal abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = [MainModule::class])
    internal abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun bindBookListActivity(): BookListActivity

    @ContributesAndroidInjector
    internal abstract fun bindBookDetailActivity(): BookDetailActivity

    @ContributesAndroidInjector(modules = [FileSystemModule::class])
    internal abstract fun bookFileSystemActivity(): FileSystemActivity

    @ContributesAndroidInjector
    internal abstract fun bindReadActivity(): ReadActivity
}