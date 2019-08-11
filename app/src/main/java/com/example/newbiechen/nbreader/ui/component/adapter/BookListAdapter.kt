package com.example.newbiechen.nbreader.ui.component.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SimpleAdapter
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.data.entity.BookEntity
import com.example.newbiechen.nbreader.databinding.ItemBookListBinding
import com.example.newbiechen.nbreader.ui.page.base.adapter.IViewHolder
import com.example.newbiechen.nbreader.ui.page.base.adapter.SimpleBindingAdapter
import com.example.newbiechen.nbreader.uilts.Constants
import com.example.newbiechen.nbreader.uilts.LogHelper
import com.example.newbiechen.nbreader.uilts.StringUtil

/**
 *  author : newbiechen
 *  date : 2019-08-05 18:30
 *  description :
 */

class BookListAdapter : SimpleBindingAdapter<BookEntity>() {

    override fun createViewHolder(type: Int): IViewHolder<BookEntity> {
        return BookListViewHolder()
    }

    inner class BookListViewHolder : IViewHolder<BookEntity> {
        private lateinit var dataBinding: ItemBookListBinding

        override fun createBinding(parent: ViewGroup): ViewDataBinding {
            dataBinding = ItemBookListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return dataBinding
        }

        override fun onBind(value: BookEntity, pos: Int) {
            dataBinding.apply {
                val context = root.context
                coverUrl = Constants.IMG_BASE_URL + value.cover
                title = value.title
                brief = context.getString(R.string.book_brief, value.author, value.minorCate)
                hot = context.getString(
                    R.string.book_hot,
                    StringUtil.number2Str(context, value.latelyFollower)
                )

                executePendingBindings()
            }
        }
    }
}