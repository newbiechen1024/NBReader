package com.example.newbiechen.nbreader.ui.page.base.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.newbiechen.nbreader.uilts.LogHelper

typealias OnItemClickListener<T> = (pos: Int, value: T) -> Unit

abstract class BaseAdapter<T> : RecyclerView.Adapter<BaseAdapter.WrapViewHolder<T>>() {
    private val mItemList: ArrayList<T> = ArrayList()

    abstract fun createViewHolder(type: Int): IViewHolder<T>

    open fun bindViewHolder(binding: ViewDataBinding, position: Int) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WrapViewHolder<T> {
        val holder = createViewHolder(viewType)
        // 创建 binding
        val binding = holder.createBinding(parent)
        return WrapViewHolder(binding, holder)
    }

    override fun getItemCount(): Int = mItemList.size

    final override fun onBindViewHolder(holder: WrapViewHolder<T>, position: Int) {
        holder.holder.onBind(getItem(position)!!, position)
        bindViewHolder(holder.binding, position)
    }

    fun getItem(pos: Int): T? {
        return if (pos < mItemList.size) mItemList[pos] else null
    }

    fun refreshItems(items: List<T>?) {
        if (items == null || items.isEmpty()) {
            return
        }

        mItemList.clear()
        mItemList.addAll(items)
        notifyDataSetChanged()

        LogHelper.i("BaseAdapter", "refreshItems")
    }

    // 用于封装的真正 ViewHolder 的 ViewHolder
    data class WrapViewHolder<T>(val binding: ViewDataBinding, val holder: IViewHolder<T>) :
        RecyclerView.ViewHolder(binding.root)
}

