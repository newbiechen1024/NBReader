package com.example.newbiechen.nbreader.ui.page.find

import androidx.databinding.ObservableArrayList
import com.example.newbiechen.nbreader.data.entity.CatalogEntity
import com.example.newbiechen.nbreader.data.repository.CatalogRepository
import com.example.newbiechen.nbreader.ui.page.base.RxViewModel
import com.example.newbiechen.nbreader.uilts.LogHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FindViewModel @Inject constructor(private val catalogRepository: CatalogRepository) : RxViewModel() {

    companion object {
        const val TAG = "FindViewModel"
    }

    val catalogList = ObservableArrayList<CatalogEntity>()

    /**
     * 加载类别列表
     */
    fun loadCatalog() {
        catalogRepository.getCatalogItems()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .firstOrError() // 获取 Flowable 的第一个元素，或者报错。因为能够确定返回值只有一个所以没问题，这是为了解决 doOnSubscribe 拿到的是 Disposable，否则拿到的是一个 Subscription
            .doOnSubscribe {
                compositeDisposable.add(it)
            }
            .subscribe({ catalogs ->
                LogHelper.i(TAG, "loadCatalog:${catalogs.size}")
                with(catalogList) {
                    clear()
                    addAll(catalogs)
                }
            }, { error ->
                // TODO:可能网络请求失败
                LogHelper.i(TAG, "error:$error")
            })
    }
}