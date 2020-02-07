package com.newbiechen.nbreader.di.module

import com.newbiechen.nbreader.uilts.rxbus.RxBus
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 *  author : newbiechen
 *  date : 2020-02-06 21:53
 *  description :工具模块
 */

@Module
class UtilModule {

    @Provides
    @Singleton
    fun provideRxBus() = RxBus.getInstance()
}