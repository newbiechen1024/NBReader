package com.newbiechen.nbreader.di.module

import com.newbiechen.nbreader.ui.page.filecatalog.FileCatalogFragment
import com.newbiechen.nbreader.ui.page.smartlookup.SmartLookupFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 *  author : newbiechen
 *  date : 2019-08-17 14:44
 *  description :
 */

@Module
abstract class FileSystemModule {
    @ContributesAndroidInjector
    internal abstract fun bindFileCatalogFragment(): FileCatalogFragment

    @ContributesAndroidInjector
    internal abstract fun bindSmartLookupFragment(): SmartLookupFragment
}