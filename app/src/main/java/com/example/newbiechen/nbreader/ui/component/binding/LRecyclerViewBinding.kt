package com.example.newbiechen.nbreader.ui.component.binding

import androidx.databinding.BindingAdapter
import com.example.newbiechen.nbreader.data.entity.BookEntity
import com.example.newbiechen.nbreader.ui.component.adapter.BookListAdapter
import com.github.jdsjlzx.recyclerview.LRecyclerView
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter

/**
 *  author : newbiechen
 *  date : 2019-08-10 19:12
 *  description :
 */

object LRecyclerViewBinding {

    @BindingAdapter("app:items")
    @JvmStatic
    fun LRecyclerView.setBookList(bookList: List<BookEntity>) {
        var lAdapter = adapter as LRecyclerViewAdapter
        (lAdapter.innerAdapter as? BookListAdapter)?.refreshItems(bookList)

        // 通知数据加载完成
        // 默认设置为 21，传入参数主要用来处理数据未满一页的情况，不考虑这种情况，设置为 0 也是 ok 的。
        refreshComplete(21)
    }
}