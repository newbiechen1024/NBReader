package com.example.newbiechen.nbreader.ui.component.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.newbiechen.nbreader.data.entity.CatalogEntity
import com.example.newbiechen.nbreader.ui.component.adapter.FindAdapter
import com.example.newbiechen.nbreader.ui.component.adapter.SmartLookupAdapter
import com.example.newbiechen.nbreader.uilts.mediastore.LocalBookInfo

object RecyclerViewBinding {

    @BindingAdapter("app:items")
    @JvmStatic
    fun RecyclerView.setCatalogs(catalogs: List<CatalogEntity>?) {
        (adapter as FindAdapter?)?.apply {
            refreshItems(catalogs)
        }
    }

    @BindingAdapter("app:items")
    @JvmStatic
    fun RecyclerView.setBookInfoGroup(groups: List<Pair<String, List<LocalBookInfo>>>?) {
        if (groups == null) {
            return
        }

        (adapter as SmartLookupAdapter?)?.apply {
            refreshAllGroup(groups)
        }
    }
}