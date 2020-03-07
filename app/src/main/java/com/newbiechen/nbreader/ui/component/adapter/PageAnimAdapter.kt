package com.newbiechen.nbreader.ui.component.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.newbiechen.nbreader.R
import com.newbiechen.nbreader.databinding.ItemPageAnimBinding
import com.newbiechen.nbreader.ui.component.adapter.base.IViewHolder
import com.newbiechen.nbreader.ui.component.adapter.base.OnItemClickListener
import com.newbiechen.nbreader.ui.component.adapter.base.SimpleBindingAdapter
import com.newbiechen.nbreader.ui.component.widget.page.PageAnimType

/**
 *  author : newbiechen
 *  date : 2020/3/7 9:25 PM
 *  description :页面动画适配器
 */

class PageAnimAdapter : SimpleBindingAdapter<PageAnimType>() {

    private var mItemClickListener: OnItemClickListener<PageAnimType>? = null

    private var mCheckedPos = 0

    override fun createViewHolder(type: Int): IViewHolder<PageAnimType> {
        return PageAnimHolder()
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener<PageAnimType>) {
        mItemClickListener = itemClickListener
    }

    inner class PageAnimHolder : IViewHolder<PageAnimType> {
        private lateinit var dataBinding: ItemPageAnimBinding

        override fun createBinding(parent: ViewGroup): ViewDataBinding {
            dataBinding = ItemPageAnimBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
            return dataBinding
        }

        override fun onBind(value: PageAnimType, pos: Int) {
            val typeId = when (value) {
                PageAnimType.NONE -> {
                    R.string.none_page_anim
                }
                PageAnimType.SIMULATION -> {
                    R.string.simulation_page_anim
                }
                PageAnimType.SLIDE -> {
                    R.string.slide_page_anim
                }
                PageAnimType.SCROLL -> {
                    R.string.scroll_page_anim
                }
                PageAnimType.COVER -> {
                    R.string.cover_page_anim
                }
            }

            dataBinding.apply {
                type = root.context.getString(typeId)
                isSelected = pos == mCheckedPos
            }

            dataBinding.root.setOnClickListener {
                mItemClickListener?.invoke(pos, value)

                mCheckedPos = pos

                // 通知刷新
                notifyDataSetChanged()
            }

            dataBinding.executePendingBindings()
        }
    }
}