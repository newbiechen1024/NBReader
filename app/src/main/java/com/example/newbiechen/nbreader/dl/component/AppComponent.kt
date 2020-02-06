package com.example.newbiechen.nbreader.dl.component

import com.example.newbiechen.nbreader.NBApplication
import com.example.newbiechen.nbreader.dl.module.DatabaseModule
import com.example.newbiechen.nbreader.dl.module.NetworkModule
import com.example.newbiechen.nbreader.dl.module.RepositoryModule
import com.example.newbiechen.nbreader.dl.module.UtilModule
import com.example.newbiechen.nbreader.dl.module.base.ActivityBindingModule
import com.example.newbiechen.nbreader.dl.module.base.AppModule
import com.example.newbiechen.nbreader.dl.module.base.ViewModelModule
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