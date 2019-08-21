package com.example.newbiechen.nbreader.ui.component.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.data.entity.BookEntity
import com.example.newbiechen.nbreader.databinding.ItemBookListBinding
import com.example.newbiechen.nbreader.ui.component.adapter.base.IViewHolder
import com.example.newbiechen.nbreader.ui.component.adapter.base.SimpleBindingAdapter
import com.example.newbiechen.nbreader.uilts.Constants
import com.example.newbiechen.nbreader.uilts.NumberUtil

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
                    NumberUtil.convertNumber(context, value.latelyFollower.toLong())
                )

                executePendingBindings()
            }
        }
    }
}