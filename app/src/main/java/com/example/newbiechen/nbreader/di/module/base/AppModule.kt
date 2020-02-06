package com.example.newbiechen.nbreader.di.module.base

import android.app.Application
import android.content.Context
import com.example.newbiechen.nbreader.NBApplication
import dagger.Binds
import dagger.Module

@Module
abstract class AppModule {

    @Binds
    abstract fun bindApplicationContext(application: NBApplication): Context

    @Binds
    abstract fun bindApplication(application: NBApplication): Application
}