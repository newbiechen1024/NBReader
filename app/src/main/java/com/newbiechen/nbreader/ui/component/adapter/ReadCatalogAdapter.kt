package com.newbiechen.nbreader.ui.component.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.newbiechen.nbreader.databinding.ItemReadCatalogyBinding
import com.newbiechen.nbreader.ui.component.adapter.base.IViewHolder
import com.newbiechen.nbreader.ui.component.adapter.base.OnItemClickListener
import com.newbiechen.nbreader.ui.component.adapter.base.SimpleBindingAdapter
import com.newbiechen.nbreader.ui.component.book.Chapter

/**
 *  author : newbiechen
 *  date : 2020-02-12 22:33
 *  description :阅读页目录
 */

class ReadCatalogAdapter : SimpleBindingAdapter<Chapter>() {

    private var mItemClickListener: OnItemClickListener<Chapter>? = null

    private var mSelectedPos = 0

    override fun createViewHolder(type: Int): IViewHolder<Chapter> {
        return ChapterViewHolder()
    }

    /**
     * 设置选中的 item
     */
    fun setSelectedItem(pos: Int) {
        mSelectedPos = pos

        notifyDataSetChanged()
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener<Chapter>) {
        mItemClickListener = itemClickListener
    }

    inner class ChapterViewHolder : IViewHolder<Chapter> {
        private lateinit var mDataBinding: ItemReadCatalogyBinding

        override fun createBinding(parent: ViewGroup): ViewDataBinding {
            mDataBinding = ItemReadCatalogyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
            return mDataBinding
        }

        override fun onBind(value: Chapter, pos: Int) {
            mDataBinding.apply {
                title = value.title
                tvChapter.isSelected = pos == mSelectedPos
            }

            mDataBinding.root.setOnClickListener {
                mItemClickListener?.invoke(pos, value)

                mSelectedPos = pos

                // 通知刷新
                notifyDataSetChanged()
            }
        }
    }
}