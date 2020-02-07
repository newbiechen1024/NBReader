package com.newbiechen.nbreader.ui.page.booklist

import android.content.Context
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import com.newbiechen.nbreader.data.entity.NetBookEntity
import com.newbiechen.nbreader.data.repository.impl.IBookListRepository
import com.newbiechen.nbreader.ui.page.base.BaseViewModel
import com.newbiechen.nbreader.ui.component.widget.StatusView
import com.newbiechen.nbreader.uilts.LogHelper
import com.newbiechen.nbreader.uilts.NetworkUtil
import com.github.jdsjlzx.view.LoadingFooter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

// 传递什么呢 error 还有 no more
typealias OnLoadMoreStateChange = (state: LoadingFooter.State) -> Unit

class BookListViewModel @Inject constructor(private val repository: IBookListRepository) : BaseViewModel() {

    companion object {
        // 每页加载的数据量
        private const val PAGE_LIMIT = 21
        private const val TAG = "BookListViewModel"
    }

    // 当前页面状态
    val pageStatus = ObservableField(StatusView.STATUS_LOADING)

    val bookList = ObservableArrayList<NetBookEntity>()

    private var curItemPos = 0
    private var totalCount = 0

    // 请求参数
    private var alias: String? = null
    private var sort: Int? = null
    private var cat: String? = null
    private var isserial: Boolean? = null
    private var updated: Int? = null

    // 刷新书籍列表
    fun refreshBookList(
        context: Context,
        alias: String,
        sort: Int,
        cat: String? = null,
        isserial: Boolean? = null,
        updated: Int? = null
    ) {
        // 存储请求参数
        this.alias = alias
        this.sort = sort
        this.cat = cat
        this.isserial = isserial
        this.updated = updated

        // 检测当前网络状态
        if (!NetworkUtil.isNetworkAvaialble(context)) {
            pageStatus.set(StatusView.STATUS_ERROR)
            return
        }

        pageStatus.set(StatusView.STATUS_LOADING)
        // 请求书籍
        addDisposable(
            repository.getBookList(alias, sort, curItemPos, PAGE_LIMIT, cat, isserial, updated)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        LogHelper.i(TAG, "result:${it.books.size}")
                        // 通知完成
                        bookList.clear()
                        bookList.addAll(it.books)
                        // 设置参数
                        totalCount = it.total
                        curItemPos += it.books.size
                    },
                    {
                        pageStatus.set(StatusView.STATUS_ERROR)
                        // 发送 toast 提示？
                    },
                    {
                        // 通知当前状态为完成状态
                        pageStatus.set(StatusView.STATUS_FINISH)
                    }
                )
        )
    }

    // 加载更多书籍
    fun loadMoreBookList(context: Context, onLoadMoreStateChange: OnLoadMoreStateChange) {
        if (alias == null || sort == null) {
            return
        }

        // 检测当前网络状态,设置错误回调
        if (!NetworkUtil.isNetworkAvaialble(context)) {
            onLoadMoreStateChange(LoadingFooter.State.NetWorkError)
            return
        }

        // 请求书籍
        addDisposable(
            repository.getBookList(alias!!, sort!!, curItemPos, PAGE_LIMIT, cat, isserial, updated)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        bookList.addAll(it.books)
                        curItemPos += it.books.size
                    },
                    {
                        onLoadMoreStateChange(LoadingFooter.State.NetWorkError)
                    },
                    {
                        if (curItemPos >= totalCount) {
                            onLoadMoreStateChange(LoadingFooter.State.NoMore)
                        }
                    }
                )
        )
    }
}