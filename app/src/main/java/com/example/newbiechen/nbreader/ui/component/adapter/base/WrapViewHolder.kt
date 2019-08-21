package com.example.newbiechen.nbreader.ui.component.adapter.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 *  author : newbiechen
 *  date : 2019-08-19 17:12
 *  description :
 */

// 用于封装的真正 ViewHolder 的 ViewHolder
data class WrapViewHolder<T>(val binding: ViewDataBinding, val holder: IViewHolder<T>) :
    RecyclerView.ViewHolder(binding.root)