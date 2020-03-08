package com.newbiechen.nbreader.ui.page.bookshelf

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import com.newbiechen.nbreader.data.entity.BookEntity
import com.newbiechen.nbreader.data.repository.BookRepository
import com.newbiechen.nbreader.ui.component.widget.StatusView
import com.newbiechen.nbreader.ui.page.base.BaseViewModel
import com.newbiechen.nbreader.uilts.LogHelper
import com.newbiechen.nbreader.uilts.rxbus.CacheBookChangedEvent
import com.newbiechen.nbreader.uilts.rxbus.RxBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 *  author : newbiechen
 *  date : 2020-02-06 20:50
 *  description :
 */

class BookShelfViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val rxBus: RxBus
) : BaseViewModel() {

    companion object {
        private const val TAG = "BookShelfViewModel"
    }

    /**
     * 缓存书籍列表
     */
    val cacheBookList = ObservableArrayList<BookEntity>()
    val pageStatus = ObservableField<Int>()

    init {
        initRxEvent()
    }

    private fun initRxEvent() {
        // 监听缓存书籍改变的事件
        addDisposable(rxBus.toObservable(CacheBookChangedEvent::class.java)
            .subscribe {
                // 更新书籍缓存
                loadCacheBooks()
            }
        )
    }

    /**
     * 加载缓存书籍
     */
    fun loadCacheBooks() {
        // 从数据库中获取缓存书籍
        addDisposable(
            bookRepository.getBooks(true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    // 清空书籍信息
                    cacheBookList.clear()
                    // 将数据添加到缓冲列表中
                    cacheBookList.addAll(it)
                    // 更新页面展示状态
                    if (cacheBookList.isNotEmpty()) {
                        pageStatus.set(StatusView.STATUS_FINISH)
                    } else {
                        pageStatus.set(StatusView.STATUS_EMPTY)
                    }
                }
        )
    }

    /**
     * 刷新缓存书籍信息
     */
    fun refreshCacheBooks() {
        // 未实现
    }
}