package com.newbiechen.nbreader.ui.component.adapter.base

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding

interface IViewHolder<T> {
    fun createBinding(parent: ViewGroup): ViewDataBinding

    fun onBind(value: T, pos: Int)
}