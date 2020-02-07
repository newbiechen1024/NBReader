package com.newbiechen.nbreader.uilts.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * ViewModel 工厂，原理：
 * 1. ViewModelFactory 会获取到 Dagger2 中注册的所有  Provider<ViewModel>> 对象
 * 2. 当用户需要指定 ViewModel 时，就会调用这个 Factory，获取 ViewModel 对应的 Provider<ViewModel>>
 * 3. 通过 Provider<ViewModel>> 获取被 Dagger2 注入过的 ViewModel。
 */
@Singleton
class ViewModelFactory @Inject constructor(
    private val modelMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val model = modelMap[modelClass]
            ?: modelMap.entries.firstOrNull { (key, _) ->
                modelClass.isAssignableFrom(key)
            }?.value
            ?: throw IllegalArgumentException("Unknown model class $modelClass")
        return model.get() as T
    }
}