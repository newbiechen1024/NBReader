package com.newbiechen.nbreader

import android.content.Context
import com.newbiechen.nbreader.di.component.DaggerAppComponent
import com.facebook.stetho.Stetho
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class NBApplication : DaggerApplication() {

    private lateinit var androidInjector: AndroidInjector<out DaggerApplication>

    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        androidInjector = DaggerAppComponent.builder()
            .application(this).build()
    }

    public override fun applicationInjector(): AndroidInjector<out DaggerApplication> = androidInjector

}