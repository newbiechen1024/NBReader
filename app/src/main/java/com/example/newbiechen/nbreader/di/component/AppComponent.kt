package com.example.newbiechen.nbreader.di.component

import com.example.newbiechen.nbreader.NBApplication
import com.example.newbiechen.nbreader.di.module.DatabaseModule
import com.example.newbiechen.nbreader.di.module.NetworkModule
import com.example.newbiechen.nbreader.di.module.RepositoryModule
import com.example.newbiechen.nbreader.di.module.UtilModule
import com.example.newbiechen.nbreader.di.module.base.ActivityBindingModule
import com.example.newbiechen.nbreader.di.module.base.AppModule
import com.example.newbiechen.nbreader.di.module.base.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class, AppModule::class,
        ActivityBindingModule::class, ViewModelModule::class,
        RepositoryModule::class, DatabaseModule::class,
        NetworkModule::class, UtilModule::class]
)
interface AppComponent : AndroidInjector<NBApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: NBApplication): Builder

        fun build(): AppComponent
    }
}