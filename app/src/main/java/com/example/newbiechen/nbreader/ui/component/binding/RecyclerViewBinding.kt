package com.example.newbiechen.nbreader.ui.component.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.newbiechen.nbreader.data.entity.CatalogEntity
import com.example.newbiechen.nbreader.ui.component.adapter.FindAdapter

object RecyclerViewBinding {
    @BindingAdapter("app:items")
    @JvmStatic
    fun RecyclerView.setCatalogs(catalogs: List<CatalogEntity>?) {

        (adapter as FindAdapter?)?.apply {
            refreshItems(catalogs)
        }
    }
}