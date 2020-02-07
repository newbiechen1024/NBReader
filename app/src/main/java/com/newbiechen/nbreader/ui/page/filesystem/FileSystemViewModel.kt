package com.newbiechen.nbreader.ui.page.filesystem

import androidx.databinding.ObservableField
import com.newbiechen.nbreader.data.entity.BookEntity
import com.newbiechen.nbreader.data.entity.LocalBookEntity
import com.newbiechen.nbreader.data.repository.BookRepository
import com.newbiechen.nbreader.ui.page.base.BaseViewModel
import com.newbiechen.nbreader.uilts.Void
import com.newbiechen.nbreader.uilts.rxbus.CacheBookChangedEvent
import com.newbiechen.nbreader.uilts.rxbus.RxBus
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 *  author : newbiechen
 *  date : 2019-08-21 17:01
 *  description :
 */
class FileSystemViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val rxBus: RxBus
) : BaseViewModel() {
    val checkedCount = ObservableField(0)
    val isCheckedAll = ObservableField(false)

    /**
     * 本地书籍到数据库
     */
    fun saveBooks(localBooks: List<LocalBookEntity>) {
        addDisposable(Single.create<Void> {
            // 将本地书籍转换成书籍信息
            val bookEntities: List<BookEntity> = localBooks.map { localEntity ->
                val bookEntity: BookEntity
                localEntity.apply {
                    bookEntity = BookEntity(
                        id.toString(), name,
                        type, path, true
                    )
                }
                bookEntity
            }

            // 存储到数据库中
            bookRepository.saveBooks(bookEntities)

            // 通知完成
            it.onSuccess(Void())
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { _ ->
                // 发送缓存书籍改变通知
                rxBus.post(CacheBookChangedEvent())
            }
        )
    }
}