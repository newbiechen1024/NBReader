package com.newbiechen.nbreader.ui.component.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.newbiechen.nbreader.R
import com.newbiechen.nbreader.data.entity.NetBookEntity
import com.newbiechen.nbreader.databinding.ItemBookListBinding
import com.newbiechen.nbreader.ui.component.adapter.base.IViewHolder
import com.newbiechen.nbreader.ui.component.adapter.base.SimpleBindingAdapter
import com.newbiechen.nbreader.uilts.Constants
import com.newbiechen.nbreader.uilts.NumberUtil

/**
 *  author : newbiechen
 *  date : 2019-08-05 18:30
 *  description :
 */

class BookListAdapter : SimpleBindingAdapter<NetBookEntity>() {

    override fun createViewHolder(type: Int): IViewHolder<NetBookEntity> {
        return BookListViewHolder()
    }

    inner class BookListViewHolder : IViewHolder<NetBookEntity> {
        private lateinit var dataBinding: ItemBookListBinding

        override fun createBinding(parent: ViewGroup): ViewDataBinding {
            dataBinding = ItemBookListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return dataBinding
        }

        override fun onBind(value: NetBookEntity, pos: Int) {
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