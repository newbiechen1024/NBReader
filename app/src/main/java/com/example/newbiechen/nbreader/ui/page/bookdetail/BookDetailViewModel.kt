package com.example.newbiechen.nbreader.ui.page.bookdetail

import android.content.Context
import androidx.databinding.ObservableField
import com.example.newbiechen.nbreader.data.entity.BookDetailWrapper
import com.example.newbiechen.nbreader.data.repository.impl.IBookDetailRepository
import com.example.newbiechen.nbreader.ui.component.widget.StatusView
import com.example.newbiechen.nbreader.ui.page.base.RxViewModel
import com.example.newbiechen.nbreader.uilts.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 *  author : newbiechen
 *  date : 2019-08-14 18:27
 *  description :
 */

class BookDetailViewModel @Inject constructor(private val repository: IBookDetailRepository) : RxViewModel() {

    val pageStatus = ObservableField(StatusView.STATUS_LOADING)
    val bookDetail = ObservableField<BookDetailWrapper>()

    fun loadBookDetail(context: Context, bookId: String) {
        // 检测当前网络状态
        if (!NetworkUtil.isNetworkAvaialble(context)) {
            pageStatus.set(StatusView.STATUS_ERROR)
            return
        }

        compositeDisposable.add(
            repository.getBookDetail(bookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        bookDetail.set(it)
                    }, {
                        pageStatus.set(StatusView.STATUS_ERROR)
                    }, {
                        pageStatus.set(StatusView.STATUS_FINISH)
                    }
                )
        )
    }
}