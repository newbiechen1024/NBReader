package com.example.newbiechen.nbreader.ui.page.base.adapter

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding

interface IViewHolder<T> {
    fun createBinding(parent: ViewGroup): ViewDataBinding

    fun onBind(value: T, pos: Int)
}