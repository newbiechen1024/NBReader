package com.example.newbiechen.nbreader.uilts.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ViewModelFactory @Inject constructor(
    private val modelMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val model = modelMap[modelClass]
            ?: modelMap.entries.firstOrNull { (key, value) ->
                modelClass.isAssignableFrom(key)
            }?.value
            ?: throw IllegalArgumentException("Unknown model class $modelClass")

        return model.get() as T
    }
}