package com.newbiechen.nbreader.ui.component.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.newbiechen.nbreader.data.entity.CatalogEntity
import com.newbiechen.nbreader.databinding.ItemFindBinding
import com.newbiechen.nbreader.ui.component.adapter.base.SimpleBindingAdapter
import com.newbiechen.nbreader.ui.component.adapter.base.IViewHolder
import com.newbiechen.nbreader.ui.component.adapter.base.OnItemClickListener
import com.newbiechen.nbreader.uilts.Constants

class FindAdapter : SimpleBindingAdapter<CatalogEntity>() {

    private var mOnItemClickListener: OnItemClickListener<CatalogEntity>? = null

    override fun createViewHolder(type: Int): IViewHolder<CatalogEntity> {
        return FindViewHolder()
    }

    inner class FindViewHolder : IViewHolder<CatalogEntity> {
        private lateinit var itemFindBinding: ItemFindBinding

        override fun createBinding(parent: ViewGroup): ViewDataBinding {
            itemFindBinding = ItemFindBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return itemFindBinding
        }

        override fun onBind(value: CatalogEntity, pos: Int) {
            itemFindBinding.apply {
                root.setOnClickListener {
                    mOnItemClickListener?.let {
                        it(pos, value)
                    }
                }
                name = value.name
                icon = if (value.bookCover.isNotEmpty()) Constants.IMG_BASE_URL + value.bookCover[0] else ""
                executePendingBindings()
            }
        }
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener<CatalogEntity>) {
        mOnItemClickListener = itemClickListener
    }
}