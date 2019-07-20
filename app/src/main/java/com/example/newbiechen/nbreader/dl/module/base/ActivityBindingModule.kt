package com.example.newbiechen.nbreader.dl.module.base

import com.example.newbiechen.nbreader.dl.module.MainModule
import com.example.newbiechen.nbreader.ui.page.main.MainActivity
import com.youtubedl.di.ActivityScoped
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * 对 Activity 进行依赖注入
 */
@Module
internal abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = [MainModule::class])
    internal abstract fun bindMainActivity(): MainActivity
}